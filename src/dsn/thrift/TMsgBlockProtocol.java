package dsn.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TTransportException;

import java.util.HashMap;
import java.util.Map;

public class TMsgBlockProtocol extends org.apache.thrift.protocol.TBinaryProtocol 
{
  private static final Map<String, String> RPC_PAIRS = new HashMap<String, String>();
  static
  {
    RPC_PAIRS.put("get", "RPC_REPLICATION_CLIENT_READ");
    RPC_PAIRS.put("put", "RPC_REPLICATION_CLIENT_WRITE");
    RPC_PAIRS.put("remove", "RPC_REPLICATION_CLIENT_WRITE");
    RPC_PAIRS.put("merge", "RPC_REPLICATION_CLIENT_WRITE");     
    RPC_PAIRS.put("query_cfg", "RPC_CM_QUERY_PARTITION_CONFIG_BY_INDEX");
  }

  private int reqHash_;
  private TMsgBlockTransport msgTransport_;

  public TMsgBlockProtocol(TMsgBlockTransport msgTransport) 
  {
    super(msgTransport);
    msgTransport_ = msgTransport;
  }
  
  public void setRequestHash(int hash)
  {
    reqHash_ = hash;
  }
  
  public void writeMessageBegin(TMessage msg) throws TException
  {
    msgTransport_.proto_writeMessageBegin(msg);
    TMessage transformedMsg = new TMessage( RPC_PAIRS.get(msg.name), msg.type, msg.seqid );
    super.writeMessageBegin(transformedMsg);
  }
  
  public void writeMessageEnd()
  {
    try 
    {
      msgTransport_.proto_writeMessageEnd(reqHash_);
    } 
    catch (TTransportException e) 
    {
      e.printStackTrace();
    }
  }
}
