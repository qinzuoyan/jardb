package dsn.apps;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class rpc_session extends rrdb.Client
{
	public static class client_seqid 
	{
		public int socket_version = -1;
		public int sequence_id = -1;
		public client_seqid(int version, int id)
		{
			socket_version = version;
			sequence_id = id;
		}
	}
	
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
	
	private static class LOCK {};

	private int socket_version_ = 0;
	private TSocket socket_ = null;
	
	private dsn.base.rpc_address addr_;
	
	private int pending_recv_seqid_ = 0;
	private boolean has_pending_ = false;
	
	private LOCK send_lock_ = new LOCK();
	private LOCK recv_lock_ = new LOCK();
	
	public rpc_session(dsn.base.rpc_address addr) {
		super(null);
		this.addr_ = addr;
	}
	
	public boolean open() {
		synchronized (this) {
			if ( socket_ != null && socket_.isOpen())
				return true;
			
			try {
				if (socket_ == null)
					socket_ = new TSocket(addr_.get_ip(), addr_.get_port());
				socket_.open();
				++socket_version_;
				
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
		}
	}
	
	public client_seqid send_message(dsn.operator.client_operator op) throws TException
	{
		synchronized (send_lock_) 
		{
			try {
				op.send(this);
				return new client_seqid(socket_version_, seqid_);
			}
			catch (TTransportException e) {
				close();
				throw e;
			}
		}
	}
	
	public void recv_message(client_seqid seqid, dsn.operator.client_operator op, cache.table_handler table) throws TException, ReplicationException
	{
		//TODO: optimize the performance 
		synchronized (recv_lock_) {
			while (true) {
				if (socket_version_ != seqid.socket_version)
					throw new TTransportException("corrupted socket");
				try {
					if (has_pending_) 
					{
						if (pending_recv_seqid_==seqid.sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							has_pending_ = false;
							recv_lock_.notifyAll();
							return;
						}
						else 
							utils.utils.waitForever(recv_lock_);
					}
					else {
						TMessage msg = iprot_.readMessageBegin();
						if (msg.seqid == seqid.sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							return;
						}
						else {
							pending_recv_seqid_ = msg.seqid;
							has_pending_ = true;
							utils.utils.waitForever(recv_lock_);
						}
					}
				}
				catch (TTransportException e) {
					recv_lock_.notifyAll();
					close();
					throw e;
				}
			}
		}
	}
}
