package dsn.apps;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import dsn.operator.*;
import dsn.thrift.*;
import dsn.replication.*;
import dsn.apps.cache.CacheLogicException;
import dsn.apps.rrdb.AsyncClient;
import dsn.base.blob;

public class rrdb_client {
	public static TServiceClient create_rpc_server_client(dsn.base.rpc_address addr) throws CacheLogicException
	{
		try 
		{
			TTransport transport = new TSocket(addr.get_ip(), addr.get_port());
			TMsgBlockTransport msgTransport = new TMsgBlockTransport(transport);
			TMsgBlockProtocol msgProtocol = new TMsgBlockProtocol(msgTransport);
			return new rrdb.Client(msgProtocol);
		}
		catch (UnknownHostException e)
		{
			throw new cache.CacheLogicException(cache.CacheLogicException.INVALID_ARGUMETNS);
		}
	}

	public static int rrdb_put(cache.table_handler table, update_request request_data) throws TException, CacheLogicException 
	{
		global_partition_id gpid = table.get_gpid(request_data.key.data);
		write_request_header header = new write_request_header(gpid, new dsn.base.task_code("RPC_RRDB_RRDB_PUT"));

		rrdb_put_operator op = new rrdb_put_operator(header, request_data);
		table.operate(op);
		return op.get_response().app_response;
	}

	public static int rrdb_remove(cache.table_handler table, blob request) throws TException, CacheLogicException {
		global_partition_id gpid = table.get_gpid(request.data);
		write_request_header header = new write_request_header(gpid, new dsn.base.task_code("RPC_RRDB_RRDB_REMOVE"));
		
		rrdb_remove_operator op = new rrdb_remove_operator(header, request);
		table.operate(op);
		return op.get_response().app_response;
	}
	
	public static int rrdb_merge(cache.table_handler table, update_request request) throws TException, CacheLogicException {
		global_partition_id gpid = table.get_gpid(request.key.data);
		write_request_header header = new write_request_header(gpid, new dsn.base.task_code("RPC_RRDB_RRDB_MERGE"));
		
		rrdb_merge_operator op = new rrdb_merge_operator(header, request);
		table.operate(op);
		return op.get_response().app_response;
	}
	
	public static read_response rrdb_get(cache.table_handler table, dsn.base.blob request) throws TException, CacheLogicException 
	{
		global_partition_id gpid = table.get_gpid(request.data);
		read_request_header header = new read_request_header(
				gpid, 
				new dsn.base.task_code("RPC_RRDB_RRDB_GET"), 
				read_semantic.ReadLastUpdate, 
				-1);
		
		rrdb_get_operator op = new rrdb_get_operator(header, request);
		table.operate(op);
		return op.get_response().app_response;
	}
	
	public static void main(String []args) throws TException, IOException
	{
	}
}
