package dsn.apps;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

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

class client_thread extends Thread {
	// so total operations = total_keys + removed_keys
	private static int total_keys = 1000;
	private static int removed_keys = 300;
	
	private String name;
	private cache.table_handler table;
	
	public client_thread(String name, cache.table_handler t)
	{
		this.name = name;
		this.table = t;
	}
	
	public void run() 
	{
		ArrayList<Integer> values = new ArrayList<Integer>(total_keys);
		
		long value_sum = 0;
		for (int i=0; i<total_keys; ++i)
			values.add((int) (Math.random()*1000));
		
		int left_put = total_keys;
		int assigned_get = 0;
		
		int key_cursor = 0;
		int total_count = total_keys + removed_keys;
		for (int i=0; i<total_count; ++i)
		{
			int t = (int) (Math.random()*(total_count-i));
			if (t < left_put) {				
				dsn.base.blob key = new dsn.base.blob(name+String.valueOf(key_cursor));
				dsn.base.blob value = new dsn.base.blob(String.valueOf( values.get(key_cursor) ));
				
				try {
					if ( rrdb_client.rrdb_put(table, new update_request(key, value)) != 0 )
						System.out.printf("%s: put failed, key=%s, value=%s\n", key.data, value.data);
				} catch (TException | CacheLogicException e) {
					e.printStackTrace();
				}
				left_put--;
				++key_cursor;				
			}
			else {
				int index = (int) (Math.random()*Math.min(key_cursor+100, total_keys));
				
				try {
					dsn.base.blob key = new dsn.base.blob(name + String.valueOf(index));
					if ( rrdb_client.rrdb_remove(table, key) != 0 )
						System.out.printf("%s: remove failed, key=%s\n", name, key.data);
					else if (index < key_cursor)
						values.set(index, 0);
				}
				catch (TException | CacheLogicException e) {
					e.printStackTrace();
				}				
			}			
		}
		
		while (assigned_get < total_keys)
		{
			try {
				dsn.base.blob key = new dsn.base.blob(name + String.valueOf(assigned_get));
				read_response resp = rrdb_client.rrdb_get(table, key);
				if (resp.error == 0) {
					value_sum += Integer.valueOf(resp.getValue());
				}
				else {
					System.out.printf("%s: get failed, key=%s, err=%d\n", name, key.data, resp.error);
				}
			}
			catch (TException | CacheLogicException e) {
				e.printStackTrace();
			}
			++assigned_get;			
		}
		
		System.out.println(name + ": value from db: " + String.valueOf(value_sum));
		long expected_value = 0;
		for (int v: values)
			expected_value += v;
		System.out.println(name + ": values expected: " + String.valueOf(expected_value));
	}
}

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
	
	public static void main(String []args) throws TException, IOException, CacheLogicException, InterruptedException
	{
		cache.cluster_handler cluster = new cache.cluster_handler("test_cluster", 10);
		cluster.add_meta("localhost", 34601);		
		cache.table_handler table = new cache.table_handler(cluster, "rrdb.instance0", new concurrency_rrdb.Factory());
		
		ArrayList<client_thread> threads = new ArrayList<client_thread>();
		for (int i=0; i<5; ++i) {
			client_thread c = new client_thread("TestThread"+String.valueOf(i)+"_", table);	
			threads.add(c);
			c.start();
		}
		
		for (int i=0; i<5; ++i)
			threads.get(i).join();
	}
}
