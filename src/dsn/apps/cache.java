package dsn.apps;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import dsn.base.error_code;
import dsn.base.error_code.error_types;
import dsn.operator.client_operator;
import dsn.replication.global_partition_id;
import dsn.replication.query_cfg_request;
import dsn.replication.query_cfg_response;
import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;

public class cache {
	private static final Logger LOGGER = LoggerFactory.getLogger(cache.class.getName());
	
	public static class CacheLogicException extends Exception
	{
		private static final long serialVersionUID = 4186015142427786503L;
		
		public static final int UNKNOWN = -1;
		public static final int NO_PRIMARY = 0;
		public static final int NO_REPLICA = 1;
		public static final int READ_TABLE_ERROR = 2;
		public static final int INVALID_ARGUMETNS = 3;
		public static final int NO_META = 5;
		public static final int REPLICATION_ERROR = 6;
		
		public int type = UNKNOWN;
		public CacheLogicException() {
			super();
		}
		public CacheLogicException(int t)
		{
			super();
			type = t;
		}
		
		public CacheLogicException(int t, String message) {
			super(message);
			type = t;
		}
		public CacheLogicException(Throwable cause)
		{
			super(cause);
		}
		public CacheLogicException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
	
	public static class table_handler {
		private cluster_handler c_;
		private String table_name_;
		private int app_id_;
		private TServiceClient[] clients_;

		private int get_partition_hash(String key) 
		{
			return 0;
		}
		
		private void query_partition_count() throws CacheLogicException, TException
		{
			dsn.replication.query_cfg_request request = new dsn.replication.query_cfg_request(table_name_, new ArrayList<Integer>());			
			dsn.replication.query_cfg_response resp = c_.call_meta(request);
			if (resp.err.errno == error_types.ERR_OK)
			{
				app_id_ = resp.app_id;
				clients_ = new TServiceClient[resp.partition_count];
				for (dsn.replication.partition_configuration pc: resp.partitions) {
					if (!pc.primary.isInvalid())
					{
						try 
						{
							clients_[pc.gpid.pidx] = rrdb_client.create_rpc_server_client(pc.primary);							
						}
						catch(CacheLogicException e)
						{
							LOGGER.warn("primary address invalid {} in gpid: {}", pc.primary.toString(), pc.gpid.toString());
						}
					}
				}
			}
			else
				throw new CacheLogicException(CacheLogicException.READ_TABLE_ERROR, resp.err.toString());
		}
		
		private void refresh_replica_server_internal(int partition_id) throws CacheLogicException, TException
		{
			dsn.replication.query_cfg_request request = new dsn.replication.query_cfg_request(table_name_, new ArrayList<Integer>());
			request.partition_indices.add(partition_id);
			
			dsn.replication.query_cfg_response resp = c_.call_meta(request);
			if (resp.err.errno == error_types.ERR_OK)
			{
				assert resp.partitions.size() == 1;
				dsn.replication.partition_configuration pc = resp.partitions.get(0);
				assert pc.gpid.pidx == partition_id;
				
				if (pc.primary.isInvalid() && pc.secondaries.isEmpty())
					throw new CacheLogicException(CacheLogicException.NO_REPLICA);
				else if (pc.primary.isInvalid())
					throw new CacheLogicException(CacheLogicException.NO_PRIMARY);
				clients_[partition_id] = rrdb_client.create_rpc_server_client(pc.primary);
			}
			else
				throw new CacheLogicException(CacheLogicException.READ_TABLE_ERROR, resp.err.toString());
		}
		
		public table_handler(cluster_handler c, String name) throws CacheLogicException, TException 
		{
			c_ = c;
			table_name_ = name;
			query_partition_count();
		}
		
		public dsn.replication.global_partition_id get_gpid(String key) 
		{
			dsn.replication.global_partition_id result = new dsn.replication.global_partition_id(app_id_, -1);
			result.pidx = get_partition_hash(key);
			return result;
		}
		
		private TServiceClient get_replica_server(int partition_id, boolean doing_refresh) throws TException, CacheLogicException 
		{
			if (partition_id < 0 || partition_id >= clients_.length)
				throw new CacheLogicException(CacheLogicException.INVALID_ARGUMETNS);
			if (clients_[partition_id] == null || doing_refresh)
				refresh_replica_server_internal(partition_id);
			return clients_[partition_id];
		}
		
		public void operate(client_operator op) throws TException, CacheLogicException
		{
			global_partition_id gpid = op.get_op_gpid();
			boolean do_refresh = false;
			while (true) {
				try {
					TServiceClient c = get_replica_server(gpid.pidx, do_refresh);
					
					if ( !c.getOutputProtocol().getTransport().isOpen() )
						c.getOutputProtocol().getTransport().open();
					op.execute(c);
					// write operation's replication error type: 
					// ERR_INVALID_DATA, the task code is not valid
					// ERR_OBJECT_NOT_FOUND, the replica not found on the meta
					// ERR_INVALID_STATE, the sever state is not primary
					// ERR_NOT_ENOUGH_MEMBER, replica count is not enough
					// ERR_CAPACITY_EXCEEDED, bounded exceeded
					
					// read opertion's replication error type:
					// ERR_INVALID_DATA, the task code is not valid
					// ERR_OBJECT_NOT_FOUND, the replica not found on the meta
					// ERR_INVALID_STATE, can't read data due to the semantic and the server state
					error_code ec = op.get_result_error();
					if (ec.errno == error_types.ERR_OK)
						return;
					else if (ec.errno == error_types.ERR_INVALID_DATA)
						throw new CacheLogicException(CacheLogicException.REPLICATION_ERROR, ec.toString());
					else
						do_refresh = true;
				}
				catch (TTransportException e) //network failure
				{
					do_refresh = true;
				}
				catch (CacheLogicException e)
				{
					switch (e.type)
					{
					case CacheLogicException.NO_PRIMARY:
					case CacheLogicException.NO_REPLICA:
						do_refresh = true;
					default:
						throw e;
					}
				}
				
				if (do_refresh) 
					utils.utils.sleepFor(1000);			
			}
		}
	}
	
	public static class cluster_handler {
		private int call_meta_retries_count_;
		private String cluster_name_;
		private int meta_leader_;
		private List<meta.Client> metas_;
		private Map<String, table_handler> tables_;

		public cluster_handler(String name, int retries_count)
		{
			call_meta_retries_count_ = retries_count;
			cluster_name_ = name;
			meta_leader_ = 0;
			metas_ = new ArrayList<meta.Client>();
			tables_ = new HashMap<String, table_handler>();
		}
		
		public void add_meta(String host, int port)
		{
			meta.Client client;
			TTransport transport = new TSocket(host, port);
			TMsgBlockTransport msgTransport = new TMsgBlockTransport(transport);
			TMsgBlockProtocol msgProtocol = new TMsgBlockProtocol(msgTransport);		
			client = new meta.Client(msgProtocol);		
			metas_.add(client);
		}
		
		table_handler open_table(String name) throws CacheLogicException, TException
		{
			table_handler handle = new table_handler(this, name);
			return handle;
		}
		
		void remove_table(String name)
		{
			tables_.remove(name);
		}
		
		dsn.replication.query_cfg_response call_leader(dsn.replication.query_cfg_request request) throws TException
		{
			meta.Client meta = metas_.get(meta_leader_);
			TTransport t = meta.getOutputProtocol().getTransport();
			if (!t.isOpen())
				t.open();
			try {
				// query configuration's error type:
				// ERR_FORWARD_TO_OTHERS: i am not meta leader
				// ERR_SERVICE_NOT_ACTIVE: i've just become the leader, the state sync isn't finished
				// ERR_OBJECT_NOT_FOUND: the table you query not found
				// ERR_INVALID_STATE: the table is just created, not available for use
				return meta.query_cfg(request);
			}
			catch (TTransportException e)
			{
				t.close();
				throw e;
			}
		}

		query_cfg_response call_metas_in_turn(query_cfg_request request) throws TException, CacheLogicException
		{
			dsn.replication.query_cfg_response resp = null;
			
			for (int j=0; j<call_meta_retries_count_; ++j) {
				int i;
				for (i=0; i<metas_.size(); ++i) {
					boolean switch_leader = false;
					try {
						resp = call_leader(request);
						if (resp.err.errno == error_types.ERR_FORWARD_TO_OTHERS)
							switch_leader = true;
					}
					catch (TTransportException e) {
						switch_leader = true;
					}
					
					if (switch_leader)
						meta_leader_ = (meta_leader_+1)%metas_.size();
					else
						break;
				}
				if (i>=metas_.size() || resp.err.errno == error_types.ERR_SERVICE_NOT_ACTIVE)
					utils.utils.sleepFor(1000);
				else
					return resp;
			}
			
			throw new CacheLogicException(CacheLogicException.NO_META);
		}
		
		dsn.replication.query_cfg_response call_meta(dsn.replication.query_cfg_request request) throws CacheLogicException, TException
		{
			if ( metas_.isEmpty() )
				throw new CacheLogicException(CacheLogicException.NO_META);
			
			return call_metas_in_turn(request);
		}
	}
}
