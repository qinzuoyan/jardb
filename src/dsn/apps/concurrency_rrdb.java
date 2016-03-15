package dsn.apps;

import java.net.UnknownHostException;
import dsn.apps.cache.CacheLogicException;
import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class concurrency_rrdb extends rrdb.Client
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
	
	public static class Factory extends cache.ConcurrencyClientFactory {
		public Factory() {}
		public TServiceClient getClient(dsn.base.rpc_address address, int partition_id)
		{
			return new concurrency_rrdb(address, partition_id);
		}
		public TServiceClient getClient(int partition_id)
		{
			return new concurrency_rrdb(partition_id);
		}
	}
	
	private static class LOCK {};

	private int socket_version_ = 0;
	private TSocket socket_ = null;
	
	private dsn.base.rpc_address addr_;
	private int partition_id_;
	
	private int pending_recv_seqid_ = 0;
	private boolean has_pending_ = false;
	
	private LOCK send_lock_ = new LOCK();
	private LOCK recv_lock_ = new LOCK();
	
	private boolean is_socket_ready() { return socket_!=null && socket_.isOpen(); }
	
	public concurrency_rrdb(int partition_id)
	{
		super(null);
		this.partition_id_ = partition_id;
	}
	
	public concurrency_rrdb(dsn.base.rpc_address addr, int partition_id) 
	{
		super(null);
		this.addr_ = addr;
		this.partition_id_ = partition_id;
	}

	private void initialize_new_socket(cache.table_handler table) throws CacheLogicException, TException 
	{
		while ( !is_socket_ready() ) {
			try {
				socket_ = new TSocket(addr_.get_ip(), addr_.get_port());
				socket_.open();
				++socket_version_;
			}
			catch (TTransportException | UnknownHostException e) {
				addr_ = table.query_rpc_address(partition_id_);
			}
		}
	}
	
	protected void receiveBase(org.apache.thrift.TBase<?,?> result, String methodName) throws TException
	{
		result.read(iprot_);
	}
	
	public client_seqid send_message(dsn.operator.client_operator op, cache.table_handler table) throws TException, CacheLogicException
	{
		synchronized (send_lock_) 
		{
			while (true) {
				try 
				{
					if (oprot_ == null)
						throw new NullPointerException("Invalid output proto");
					op.send(this);
					return new client_seqid(socket_version_, seqid_);
				}
				catch (TTransportException | NullPointerException e)
				{
					synchronized(this) 
					{
						initialize_new_socket(table);
						TMsgBlockTransport t = new TMsgBlockTransport(socket_);
						oprot_ = new TMsgBlockProtocol(t);
					}
				}
			}
		}
	}
	
	public void recv_message(client_seqid seqid, dsn.operator.client_operator op, cache.table_handler table) throws TException, CacheLogicException
	{
		synchronized (recv_lock_) {
			while (true) {
				try {
					if (iprot_ == null) {
						synchronized (this) {
							if (iprot_ == null)
								iprot_ = new TBinaryProtocol(socket_);
						}
					}
					if (has_pending_) 
					{
						if (pending_recv_seqid_==seqid.sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							has_pending_ = false;
							recv_lock_.notifyAll();
							return;
						}
						else {
							try {
								recv_lock_.wait();
							}
							catch (InterruptedException e) {
							}
						}
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
							try {
								recv_lock_.wait();
							}
							catch (InterruptedException e) {								
							}
						}
					}
				}
				catch (TTransportException e) {
					synchronized (this) {
						recv_lock_.notifyAll();
						if (socket_version_ != seqid.socket_version)
							throw new TTransportException("connection corrupted");
						else {
							initialize_new_socket(table);
							iprot_ = new TBinaryProtocol(socket_);
						}
					}
				}
			}
		}
	}
}
