package rrdb;

import org.apache.thrift.TException;

import dsn.apps.*;
import dsn.base.blob;
import dsn.operator.rrdb_get_operator;
import dsn.operator.rrdb_merge_operator;
import dsn.operator.rrdb_put_operator;
import dsn.operator.rrdb_remove_operator;

import dsn.replication.global_partition_id;
import dsn.replication.read_request_header;
import dsn.replication.read_semantic;
import dsn.replication.write_request_header;

public class Table {
  private cache.table_handler table_;
  Table(cache.cluster_handler cluster, String tableName) throws ReplicationException, TException
  {
    table_ = cluster.open_table(tableName, new rpc_session.Factory(), new cache.default_hasher());
  }
  Table(cache.cluster_handler cluster, String tableName, cache.key_hash hash_function) throws ReplicationException, TException
  {
    table_ = cluster.open_table(tableName, new rpc_session.Factory(), hash_function);
  }
  
  public cache.table_handler getRawHandler() 
  {
    return table_;
  }
  
  public int put(update_request request_data) throws TException, ReplicationException 
  {
    global_partition_id gpid = table_.get_gpid(request_data.key.data);
    write_request_header header = new write_request_header(gpid, new dsn.base.task_code("RPC_RRDB_RRDB_PUT"));

    rrdb_put_operator op = new rrdb_put_operator(header, request_data);
    table_.operate(op);
    return op.get_response().app_response;
  }

  public int remove(blob request) throws TException, ReplicationException {
    global_partition_id gpid = table_.get_gpid(request.data);
    write_request_header header = new write_request_header(gpid, new dsn.base.task_code("RPC_RRDB_RRDB_REMOVE"));
    
    rrdb_remove_operator op = new rrdb_remove_operator(header, request);
    table_.operate(op);
    return op.get_response().app_response;
  }
  
  public int merge(update_request request) throws TException, ReplicationException {
    global_partition_id gpid = table_.get_gpid(request.key.data);
    write_request_header header = new write_request_header(gpid, new dsn.base.task_code("RPC_RRDB_RRDB_MERGE"));
    
    rrdb_merge_operator op = new rrdb_merge_operator(header, request);
    table_.operate(op);
    return op.get_response().app_response;
  }
  
  public read_response get(dsn.base.blob request) throws TException, ReplicationException 
  {
    global_partition_id gpid = table_.get_gpid(request.data);
    read_request_header header = new read_request_header(
        gpid, 
        new dsn.base.task_code("RPC_RRDB_RRDB_GET"), 
        read_semantic.ReadLastUpdate, 
        -1);
    
    rrdb_get_operator op = new rrdb_get_operator(header, request);
    table_.operate(op);
    return op.get_response().app_response;
  }
}
