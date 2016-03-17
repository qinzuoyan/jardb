package dsn.apps;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import dsn.base.error_code;
import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

public class rpc_session extends rrdb.Client
{
	public static class request_signature 
	{
		public long session_signature = -1;
		public int sequence_id = -1;
		
		public request_signature(long session_signature, int id)
		{
			this.session_signature = session_signature;
			this.sequence_id = id;
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
	
	private static class circular_queue {
		public final int Capacity;

		private final int Mask;
		private Object[] op_;
		private int cursor_ = 0;
		private int count_ = 0;
		
		public int start_seqid_ = 0;
		
		public int distance(int to_seqid)
		{
			if (start_seqid_ <= to_seqid)
				return to_seqid - start_seqid_;
			// handle the overflow of sequence id
			else
				return Integer.MAX_VALUE - start_seqid_ + to_seqid - Integer.MIN_VALUE + 1;			
		}
		
		public circular_queue(int power2, int start_seqid)
		{
			Capacity = (1 << power2);
			Mask = Capacity - 1;
			start_seqid_ = start_seqid;
			
			op_ = new Object[Capacity];
		}
		
		public Object get(int pos, boolean is_remove_it)
		{
			int offset = (pos + cursor_) & Mask;
			if ( is_remove_it ) {
				Object old = op_[offset];
				op_[offset] = null;
				if (old != null) 
					--count_;
				return old;
			}
			else
				return op_[offset];
		}
		
		public void put(int pos, Object obj)
		{
			int offset = (pos + cursor_) & Mask;
			assert op_[offset] == null;
			op_[offset] = obj;
			++count_;
		}
		
		public Object pop()
		{
			Object old = op_[cursor_];
			++start_seqid_;
			cursor_ = ((cursor_ + 1)&Mask);
			if (old != null)
				--count_;
			return old;
		}
		
		public int count() 
		{
			return count_;
		}
		
		public void reset(int start_seqid)
		{
			count_ = 0;
			start_seqid_ = start_seqid;
			cursor_ = 0;
			for (int i=0; i<Capacity; ++i)
				op_[i] = null;
		}
	}

	private final Object send_lock_ = new Object();
	private long signature_;
	private TSocket socket_ = null;	
	private dsn.base.rpc_address addr_;
	
	private final Object recv_lock_ = new Object();
	private int pending_recv_seqid_ = 0;
	private boolean has_pending_ = false;
	private circular_queue pending_ops_fast_;
	private Map<Integer, Object> pending_ops_slow_;
	
	public rpc_session(dsn.base.rpc_address addr) {
		super(null);
		this.addr_ = addr;
		//seqid_ = (int)(Math.random()*Integer.MAX_VALUE);
		seqid_ = 0;
		pending_ops_fast_ = new circular_queue(12, seqid_);
		pending_ops_slow_ = new TreeMap<Integer, Object>();
		
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
	
	public request_signature send_message(dsn.operator.client_operator op) throws TException
	{
		synchronized (send_lock_) 
		{
			try {
				op.send(this);
				assert seqid_+1 != pending_ops_fast_.start_seqid_;
				return new request_signature(signature_, seqid_);
			}
			catch (TTransportException e) {
				close();
				throw e;
			}
		}
	}
	
	public void recv_message(request_signature seqid, dsn.operator.client_operator op, cache.table_handler table) throws TException
	{
		//TODO: optimize the performance 
		synchronized (recv_lock_) {
			while (true) {
				if (signature_ != seqid.session_signature)
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
							utils.tool_function.waitForever(recv_lock_);
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
							utils.tool_function.waitForever(recv_lock_);
						}
					}
				}
				catch (TException e) {
					recv_lock_.notifyAll();
					if (e instanceof TTransportException && signature_ == seqid.session_signature)
						close();
					throw e;
				}
			}
		}
	}
	
	private void reset_pending()
	{
		has_pending_ = false;
		pending_ops_fast_.reset(seqid_);
		pending_ops_slow_.clear();
	}
	
	private void handle_pending_ops(request_signature seqid) throws TException
	{
		while ( pending_ops_fast_.count()>0 || !pending_ops_slow_.isEmpty())
		{
			TMessage msg = iprot_.readMessageBegin();
			dsn.operator.client_operator pending_op = 
					(dsn.operator.client_operator)get_pending_op(msg.seqid, true);
			if (pending_op != null) {
				pending_op.recv_data(iprot_);
				iprot_.readMessageEnd();
				utils.tool_function.notify(pending_op.notifier);
			}
			else {
				has_pending_ = true;
				pending_recv_seqid_ = msg.seqid;
				break;
			}
		}
	}
	
	private Object get_pending_op(int sequence_id, boolean is_remove_it)
	{
		int dist = pending_ops_fast_.distance(sequence_id);
		if (dist >= pending_ops_fast_.Capacity)
			return null;
		if (dist >= 0)
			return pending_ops_fast_.get(dist, is_remove_it);
		Integer key = new Integer(sequence_id);
		Object obj =  pending_ops_slow_.get(key);
		if (obj != null && is_remove_it)
			pending_ops_slow_.remove(key);
		return obj;
	}
	
	private void put_pending_op(int sequence_id, dsn.operator.client_operator op)
	{
		int d = pending_ops_fast_.distance(sequence_id);
		if (d < 0) {
			pending_ops_slow_.put(new Integer(sequence_id), op);
		}
		else if (d < pending_ops_fast_.Capacity)
			pending_ops_fast_.put(d, op);
		else {
			while (d >= pending_ops_fast_.Capacity) {
				Object first_op = pending_ops_fast_.pop();
				if (first_op != null)
					pending_ops_slow_.put(new Integer(pending_ops_fast_.start_seqid_), first_op);
				--d;
			}
			pending_ops_fast_.put(d, op);
		}
	}
	
	public void recv_message2(request_signature seqid, dsn.operator.client_operator op, cache.table_handler table) throws TException
	{
		synchronized (recv_lock_) {
			// TODO[BUG]: an operation with old signature may be put to pending_list
			if (signature_ != seqid.session_signature) {
				op.get_result_error().set_error_type(error_code.error_types.ERR_TIMEOUT);
				return;
			}
			try {
				if (has_pending_)
				{
					if (pending_recv_seqid_ == seqid.sequence_id)
					{
						has_pending_ = false;
						op.recv_data(iprot_);
						iprot_.readMessageEnd();
						handle_pending_ops(seqid);
						return;
					}
				}
				else {
					boolean myself_handled = false;
					while ( !myself_handled || pending_ops_fast_.count() > 0 || !pending_ops_slow_.isEmpty()) {
						TMessage msg = iprot_.readMessageBegin();
						if ( !myself_handled && msg.seqid == seqid.sequence_id) {
							op.recv_data(iprot_);
							iprot_.readMessageEnd();
							myself_handled = true;
						}
						else {
							dsn.operator.client_operator another = (dsn.operator.client_operator)get_pending_op(msg.seqid, true);
							if (another != null) {
								another.recv_data(iprot_);
								iprot_.readMessageEnd();
								utils.tool_function.notify(another.notifier);
							}
							else {
								has_pending_ = true;
								pending_recv_seqid_ = msg.seqid;
								break;
							}
						}
					}
					if (myself_handled) 
						return;
				}
				put_pending_op(seqid.sequence_id, op);
			}
			catch (TException e)
			{
				if (signature_ == seqid.session_signature) 
				{
					dsn.operator.client_operator first = null;
					while ( pending_ops_fast_.count() != 0)
					{
						first = (dsn.operator.client_operator)pending_ops_fast_.pop();
						first.get_result_error().set_error_type(error_code.error_types.ERR_TIMEOUT);
						utils.tool_function.notify(first);
					}
					for (Map.Entry<Integer, Object> entry: pending_ops_slow_.entrySet())
					{
						first = (dsn.operator.client_operator)entry.getValue();
						first.get_result_error().set_error_type(error_code.error_types.ERR_TIMEOUT);
						utils.tool_function.notify(first);
					}
					close();
				}
				throw e;
			}
		}
		synchronized (op.notifier) {
			utils.tool_function.waitForever(op.notifier);
		}
	}
}
