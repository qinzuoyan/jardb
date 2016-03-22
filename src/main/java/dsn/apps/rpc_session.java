package dsn.apps;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;
import dsn.utils.threads;
import dsn.utils.tools;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class rpc_session extends rrdb.Client {
    public static class Factory {
        private static Map<dsn.base.rpc_address, rpc_session> client_map_ = new HashMap<dsn.base.rpc_address, rpc_session>();

        public Factory() {
        }

        public rpc_session getClient(dsn.base.rpc_address addr) {
            rpc_session result = null;
            synchronized (client_map_) {
                result = client_map_.get(addr);
                if (result == null) {
                    result = new rpc_session(addr);
                    client_map_.put(addr, result);
                }
            }

            if (result.open() == false)
                return null;
            return result;
        }
    }

    private Logger logger = null;
    private final Object send_lock_ = new Object();
    private volatile long signature_;
    private TSocket socket_ = null;
    private dsn.base.rpc_address addr_;

    private final Object recv_lock_ = new Object();
    private int pending_recv_seqid_ = 0;
    private boolean has_pending_ = false;

    private final TreeMap<Integer, Object> pending_ops_ = new TreeMap<Integer, Object>();

    public rpc_session(dsn.base.rpc_address addr) {
        super(null);
        this.addr_ = addr;
        seqid_ = (int) (Math.random() * Integer.MAX_VALUE);
        signature_ = ((long) (this.hashCode()) << 32);
        logger = LoggerFactory.getLogger(this.getClass().getName() + "@" + addr.toString());
    }

    public boolean open() {
        synchronized (this) {
            if (socket_ != null && socket_.isOpen())
                return true;

            try {
                if (socket_ == null)
                    socket_ = new TSocket(addr_.get_ip(), addr_.get_port());
                socket_.open();
                ++signature_;

                logger.debug("open socket ok, current signature: {}", signature_);
                TMsgBlockTransport t = new TMsgBlockTransport(socket_);
                oprot_ = new TMsgBlockProtocol(t);
                iprot_ = new TBinaryProtocol(t);
                return true;
            } catch (TTransportException e) {
                if (socket_ != null)
                    socket_.close();
                return false;
            } catch (UnknownHostException e) {
                if (socket_ != null)
                    socket_.close();
                return false;
            }
        }
    }

    public long get_session_signature() {
        return signature_;
    }

    public int send_message(dsn.operator.client_operator op) throws TException {
        synchronized (send_lock_) {
            try {
                //Thrift protocol write can generate two types of exception
                //1. TTransportException
                //   1.1 socket not open
                //   1.2 IO exception
                //2. TException: marshal error, mostly because of the jvm
                op.send(this);
                return seqid_;
            } catch (TTransportException e) {
                logger.info("send to gpid {} with exception {}", op.get_op_gpid().toString(), e.getType());
                synchronized (this) {
                    if (e.getType() == TTransportException.NOT_OPEN) {
                        logger.debug("socket not open, may be closed by others");
                    } else {
                        logger.debug("socket error: {}", e.getCause());
                        if (socket_.isOpen())
                            socket_.close();
                    }
                }
                throw e;
            }
        }
    }

    public void recv_message(int sequence_id, long signature, dsn.operator.client_operator op) throws TException {
        //TODO: optimize the performance
        synchronized (recv_lock_) {
            while (true) {
                if (signature_ != signature) {
                    op.set_result_error(dsn.base.error_code.error_types.ERR_TIMEOUT);
                    return;
                }
                try {
                    if (has_pending_) {
                        if (pending_recv_seqid_ == sequence_id) {
                            op.recv_data(iprot_);
                            iprot_.readMessageEnd();
                            has_pending_ = false;
                            recv_lock_.notifyAll();
                            return;
                        } else
                            tools.waitForever(recv_lock_);
                    } else {
                        TMessage msg = iprot_.readMessageBegin();
                        if (msg.seqid == sequence_id) {
                            op.recv_data(iprot_);
                            iprot_.readMessageEnd();
                            return;
                        } else {
                            pending_recv_seqid_ = msg.seqid;
                            has_pending_ = true;
                            tools.waitForever(recv_lock_);
                        }
                    }
                }
                //thrift transport read error:
                //1.TProtocolException, the protocol not match, or unmarshal error
                //2.TTransportException
                //   a. NotOpen, so the socket is closed by others
                //   b. Unknown, the socket is reset by the remote side
                //3. TException, unmarshal error, mostly because of the jvm
                catch (TException e) {
                    logger.info("recv message with exception {}, sequence id {}", e.getMessage(), sequence_id);
                    boolean close_the_socket = true;

                    if (e instanceof TTransportException && ((TTransportException) e).getType() == TTransportException.NOT_OPEN)
                        close_the_socket = false;
                    synchronized (this) {
                        //well, if exception not match, it means that we are reading a wrong socket,
                        //so we shouldn't do anything except for notify other pending threads
                        if (signature_ != signature)
                            close_the_socket = false;
                        if (close_the_socket && socket_.isOpen())
                            socket_.close();
                    }

                    recv_lock_.notifyAll();
                    throw e;
                }
            }
        }
    }

    private Object get_pending_op(int sequence_id, boolean is_remove_it) {
        Integer key = new Integer(sequence_id);
        Object value = pending_ops_.get(key);
        if (value != null && is_remove_it)
            pending_ops_.remove(key);
        return value;
    }

    private void put_pending_op(int sequence_id, Object notifier) {
        pending_ops_.put(new Integer(sequence_id), notifier);
    }

    private void notify_all() {
        for (Entry<Integer, Object> entry : pending_ops_.entrySet())
            tools.notify(entry.getValue());
        pending_ops_.clear();
    }

    public void recv_message2(int sequence_id, long signature, dsn.operator.client_operator op) throws TException {
        Object notifier = threads.get_notifier();
        synchronized (notifier) {
            while (true) {
                if (this.signature_ != signature) {
                    op.set_result_error(dsn.base.error_code.error_types.ERR_TIMEOUT);
                    return;
                }
                synchronized (recv_lock_) {
                    try {
                        if (has_pending_) {
                            if (pending_recv_seqid_ == sequence_id) {
                                has_pending_ = false;
                                op.recv_data(iprot_);
                                iprot_.readMessageEnd();
                                notify_all();
                                return;
                            }
                        } else {
                            TMessage msg = iprot_.readMessageBegin();
                            if (msg.seqid == sequence_id) {
                                op.recv_data(iprot_);
                                iprot_.readMessageEnd();
                                return;
                            } else {
                                has_pending_ = true;
                                pending_recv_seqid_ = msg.seqid;
                                Object another = get_pending_op(msg.seqid, true);
                                if (another != null) {
                                    tools.notify(another);
                                }
                            }
                        }
                        put_pending_op(sequence_id, notifier);
                    } catch (TException e) {
                        logger.info("recv message with exception {}, sequence id {}", e.getMessage(), sequence_id);
                        boolean close_the_socket = true;

                        if (e instanceof TTransportException && ((TTransportException) e).getType() == TTransportException.NOT_OPEN)
                            close_the_socket = false;
                        synchronized (this) {
                            //well, if signature not match, it means that we are reading a wrong socket,
                            //so we shouldn't do anything except for notify other pending threads
                            if (signature_ != signature)
                                close_the_socket = false;
                            if (close_the_socket && socket_.isOpen())
                                socket_.close();
                        }
                        notify_all();
                        throw e;
                    }
                }
                tools.waitForever(notifier);
            }
        }
    }
}
