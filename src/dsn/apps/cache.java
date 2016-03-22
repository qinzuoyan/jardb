package dsn.apps;

import java.util.*;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;

import dsn.base.error_code.error_types;
import dsn.operator.client_operator;
import dsn.replication.global_partition_id;
import dsn.replication.query_cfg_request;
import dsn.replication.query_cfg_response;
import dsn.thrift.TMsgBlockProtocol;
import dsn.thrift.TMsgBlockTransport;

public class cache 
{
  public interface key_hash
  {
    long hash(String key);
  }

  public static class default_hasher implements key_hash {
    @Override
    public long hash(String key) {
      return utils.tools.dsn_crc64(key.getBytes());
    }
  }
  
  public static final class table_handler 
  {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(table_handler.class);
    private cluster_handler c_;
    private String table_name_;
    private int app_id_;
    private rpc_session.Factory factory_;
    private key_hash hasher_;
    private rpc_session[] clients_;

    private static final boolean FORCE_SYNC = true;
    
    private void query_partition_count() throws ReplicationException, TException
    {
      dsn.replication.query_cfg_request request = new dsn.replication.query_cfg_request(table_name_, new ArrayList<Integer>());     
      dsn.replication.query_cfg_response resp = c_.call_meta(request);
      if (resp.err.errno == error_types.ERR_OK)
      {
        app_id_ = resp.app_id;
        clients_ = new rpc_session[resp.partition_count];
        for (dsn.replication.partition_configuration pc: resp.partitions) {
          if (!pc.primary.isInvalid())
          {
            clients_[pc.gpid.pidx] = factory_.getClient(pc.primary);
          }
          else
            clients_[pc.gpid.pidx] = null;
        }
        logger.debug("query table {} from meta, got {} partitions", table_name_, resp.partition_count);
      }
      else
        throw new ReplicationException(error_types.ERR_READ_TABLE_FAILED, resp.err.toString());
    }
    
    private rpc_session get_client(int pidx, boolean force_sync, long old_signature) throws TException, ReplicationException
    {
      rpc_session result = null;
      if ( !force_sync ) {
        result = clients_[pidx];
        if (result != null)
          return result;
      }
      
      synchronized (clients_) {
        result = clients_[pidx];
        if (result != null && result.get_session_signature() != old_signature)
          return result;
        
        result = null;
        while (result == null) {
          try {
            dsn.base.rpc_address address = query_rpc_address(pidx);
            result = factory_.getClient(address);
            clients_[pidx] = result;
          }
          catch(ReplicationException e) 
          {
            if (e.err_type == error_types.ERR_NO_PRIMARY)
              utils.tools.sleepFor(1000);
            else
              throw e;
          }
        }
        return result;
      }
    }
    
    private dsn.base.rpc_address query_rpc_address(int partition_id) throws ReplicationException, TException
    {
      dsn.replication.query_cfg_request req = new dsn.replication.query_cfg_request(
          table_name_, 
          new ArrayList<Integer>());
      req.partition_indices.add(partition_id);
      dsn.replication.query_cfg_response resp = c_.call_meta(req);
      if (resp.err.errno == error_types.ERR_OK)
      {
        dsn.replication.partition_configuration pc = resp.partitions.get(0);
        logger.debug("query partition cfg resp:{}", pc.toString());
        if (pc.primary.isInvalid() && pc.secondaries.isEmpty())
          throw new ReplicationException(error_types.ERR_NO_REPLICA);
        else if (pc.primary.isInvalid())
          throw new ReplicationException(error_types.ERR_NO_PRIMARY);
        return pc.primary;
      }
      else
        throw new ReplicationException(error_types.ERR_READ_TABLE_FAILED, resp.err.toString());
    }
    
    table_handler(cluster_handler c, String name, rpc_session.Factory factory, key_hash hasher) throws ReplicationException, TException
    {
      c_ = c;
      table_name_ = name;
      factory_ = factory;
      hasher_ = hasher;
      query_partition_count();
    }
    
    public dsn.replication.global_partition_id get_gpid(String key) 
    {
      dsn.replication.global_partition_id result = new dsn.replication.global_partition_id(app_id_, -1);
      result.pidx = (int)(hasher_.hash(key)%clients_.length);
      return result;
    }
    
    // possible exception
    // 1. TException, marshall/unmarshall error
    // 2. ReplicationException: 
    //    types:
    //    (1) NO_META, communicate to meta server failed
    //    (2) READ_TABLE_ERROR, read table failed
    //    (3) NO_REPLICA, no replica server for some partition
    //    (4) REPLICATION_ERROR, may be message data corrupted
    public void operate(client_operator op) throws TException, ReplicationException
    {
      global_partition_id gpid = op.get_op_gpid();

      rpc_session c = get_client(gpid.pidx, !FORCE_SYNC, -1);
      long signature_ = c.get_session_signature();
            
      int sequence;
      
      while ( true ) {
        try {
          sequence = c.send_message(op);
          break;
        }
        catch (TTransportException e) {
          logger.info("error{} for replication server of {}", e.getMessage(), gpid.toString());
          c = get_client(gpid.pidx, FORCE_SYNC, signature_);
        }
      }
      
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
      while ( true ) {
        try {
          c.recv_message2(sequence, signature_, op);        
          switch (op.get_result_error().errno)
          {
          case ERR_OK:
            return;
          case ERR_INVALID_STATE:
          case ERR_OBJECT_NOT_FOUND:
          case ERR_TIMEOUT:
            throw new TTransportException(op.get_result_error().toString());
          default:
            throw new ReplicationException(error_types.ERR_INVALID_DATA);         
          }
        }
        //well, the handling of send and receive are different
        //if sending a message with an exception which is not an TTransportException
        //we don't close the socket, and don't flush the client
        //but for the receiving part, we'd better close the socket as we can't do more
        //if we got a wrong message
        catch (TException e) {
          logger.info("error{} for replication server of {}", e.getMessage(), gpid.toString());
          c = get_client(gpid.pidx, FORCE_SYNC, signature_);
        }
      }
    }
   }
  
  public static final class cluster_handler 
  {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(cluster_handler.class);
    private int call_meta_retries_count_;
    private int meta_leader_;
    private List<String> meta_address_;
    private List<meta.Client> metas_;
    private Map<String, table_handler> tables_;

    private dsn.replication.query_cfg_response call_leader(dsn.replication.query_cfg_request request) throws TException
    {
      meta.Client meta = metas_.get(meta_leader_);
      logger.debug("call meta leader, address: {}, request: {}", meta_address_.get(meta_leader_), request.toString());
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

    private query_cfg_response call_metas_in_turn(query_cfg_request request) throws TException, ReplicationException
    {
      synchronized (this) {
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
            
            if (switch_leader) {
              meta_leader_ = (meta_leader_+1)%metas_.size();
              logger.debug("switch to new leader {}", meta_address_.get(meta_leader_));
            }
            else
              break;
          }
          if (i>=metas_.size() || resp.err.errno == error_types.ERR_SERVICE_NOT_ACTIVE)
          {
            logger.info("can't connect to meta temporarily, coz {}, just wait for a while", 
                i>=metas_.size()?"no meta ready":"leader not active");
            utils.tools.sleepFor(1000);
          }
          else
            return resp;
        }
      }
      
      throw new ReplicationException(error_types.ERR_NO_META_SERVER);
    }
    
    public cluster_handler(String name, int retries_count)
    {
      call_meta_retries_count_ = retries_count;
      meta_leader_ = 0;
      metas_ = new ArrayList<meta.Client>();
      meta_address_ = new ArrayList<String>();
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
      meta_address_.add( host + ":" + String.valueOf(port) );
    }
    
    public table_handler open_table(String name, rpc_session.Factory factory, key_hash hasher) throws ReplicationException, TException
    {
      table_handler handle = new table_handler(this, name, factory, hasher);
      synchronized (this) 
      {
        tables_.put(name, handle);
      }
      return handle;
    }
    
    public void remove_table(String name)
    {
      synchronized (this) 
      {
        tables_.remove(name);     
      }
    }
    
    public dsn.replication.query_cfg_response call_meta(dsn.replication.query_cfg_request request) throws ReplicationException, TException
    {
      if ( metas_.isEmpty() )
      {
        logger.warn("meta server list is empty");
        throw new ReplicationException(error_types.ERR_NO_META_SERVER);
      }
      return call_metas_in_turn(request);
    }
  }
}
