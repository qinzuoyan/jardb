package dsn.apps;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class rpc_session extends rrdb.Client
{
	public static class Factory {
		private static Map<dsn.base.rpc_address, rpc_session> client_map_ = new HashMap<dsn.base.rpc_address, rpc_session>();
		
		public Factory() {}		
		public rpc_session getClient(dsn.base.rpc_address addr) {
			rpc_session result = null;
			synchronized (client_map_) {
				result = client_map_.get(addr);
				if (result == null) {
					result = new rpc_session(addr);
					client_map_.put(addr, result);
				}
			}
			
			if ( result.open() == false)
				return null;
			return result;
		}
	}

	private final Object send_lock_ = new Object();
	private long signature_;
	private TSocket socket_ = null;	
	private dsn.base.rpc_address addr_;
	
	private final Object recv_lock_ = new Object();
	private int pending_recv_seqid_ = 0;
	private boolean has_pending_ = false;
	
	private final TreeMap<Integer, dsn.operator.client_operator> pending_ops_ = new TreeMap<Integer, dsn.operator.client_operator>();
	public rpc_session(dsn.base.rpc_address addr) {
		super(null);
		this.addr_ = addr;
		//seqid_ = (int)(Math.random()*Integer.MAX_VALUE);
		seqid_ = 0;
		
		signature_ = ((long)(this.hashCode()) << 32);
	}
	
	public boolean open() {
		synchronized (this) {
			if ( socket_ != null && socket_.isOpen())
				return true;
			
			try {
				if (socket_ == null)
					socket_ = new TSocket(addr_.get_ip(), addr_.get_port());
				socket_.open();
				++signature_;
				
				TMsgBlockTransport t = new TMsgBlockTransport(socket_);
				oprot_ = new TMsgBlockProtocol(t);
				iprot_ = new TBinaryProtocol(t);
				return true;
			}
			catch (TTransportException | UnknownHostException e) {
				if (socket_ != null)
					socket_.close();
				return false;
			}
		}
	}
	
	public void close() {
		synchronized (this) {
			socket_.close();
			reset_pending();
		}
	}
	
	public long get_session_signature()
	{
		return signature_;
	}
	
	public int send_message(dsn.operator.client_operator op) throws TException
	{
		synchronized (send_lock_) 
		{
			try {
				op.send(this);
				return seqid_;
			}
			catch (TTransportException e) {
				close();
				throw e;
			}
		}
	}
	
	public void recv_message(int sequence_id, long signature, dsn.operator.client_operator op) throws TException
	{
		//TODO: optimize the performance 
		synchronized (recv_lock_) {
			while (true) {
				if (signature_ != signature)
					throw new TTransportException("corrupted socket");
				try {
					if (has_pending_) 
					{
						if (pending_recv_seqid_==sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							has_pending_ = false;
							recv_lock_.notifyAll();
							return;
						}
						else 
							utils.tool_function.waitForever(recv_lock_);
					}
					else {
						TMessage msg = iprot_.readMessageBegin();
						if (msg.seqid == sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							return;
						}
						else {
							pending_recv_seqid_ = msg.seqid;
							has_pending_ = true;
							utils.tool_function.waitForever(recv_lock_);
						}
					}
				}
				catch (TException e) {
					recv_lock_.notifyAll();
					if (e instanceof TTransportException && signature_ == signature)
						close();
					throw e;
				}
			}
		}
	}
	
	private void reset_pending()
	{
		pending_ops_.clear();
	}
	
	private dsn.operator.client_operator get_pending_op(int sequence_id, boolean is_remove_it)
	{
		Integer key = new Integer(sequence_id);
		dsn.operator.client_operator value = pending_ops_.get(key);
		if (value != null)
			pending_ops_.remove(key);
		return value;		
	}
	
	private void put_pending_op(int sequence_id, dsn.operator.client_operator op)
	{
		pending_ops_.put(new Integer(sequence_id), op);
	}

	private void notify_all()
	{
		for (Entry<Integer, dsn.operator.client_operator> entry: pending_ops_.entrySet())
			utils.tool_function.notify(entry.getValue().notifier);
		pending_ops_.clear();
	}
	
	public void recv_message2(int sequence_id, long signature, dsn.operator.client_operator op) throws TException
	{
		synchronized (op.notifier) {
			while (true) {
				synchronized (recv_lock_) {
					if (has_pending_)
					{
						if (pending_recv_seqid_ == sequence_id) {
							has_pending_ = false;
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							notify_all();
							return;
						}
					}
					else 
					{
						TMessage msg = iprot_.readMessageBegin();
						if (msg.seqid == sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							return;
						}
						else {
							has_pending_ = true;
							pending_recv_seqid_ = msg.seqid;
							dsn.operator.client_operator another = get_pending_op(msg.seqid, true);
							if (another != null) {
								utils.tool_function.notify(op.notifier);
							}
						}
					}
					put_pending_op(sequence_id, op);
				}
				utils.tool_function.waitForever(op.notifier);
			}
		}
	}
}
