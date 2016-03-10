package dsn.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TTransport;
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
	private Map<Integer, String> rpcidPairs_;

	private String fetchRpcName(int seqid)
	{
		if (rpcidPairs_.containsKey(seqid))
		{
			String result = rpcidPairs_.get(seqid);
			rpcidPairs_.remove(seqid);
			return result;
		}
		return null;
	}
	
	public static class Factory implements TProtocolFactory {
		@Override
		public TProtocol getProtocol(TTransport transport) {
			TMsgBlockTransport trans = new TMsgBlockTransport(transport);
			return new TMsgBlockProtocol(trans);
		}
		public Factory() {
		}
	}
	
	public TMsgBlockProtocol(TMsgBlockTransport msgTransport) 
	{
		super(msgTransport);
		msgTransport_ = msgTransport;
		rpcidPairs_ = new HashMap<Integer, String>();
	}
	
	public void setRequestHash(int hash)
	{
		reqHash_ = hash;
	}
	
	public void writeMessageBegin(TMessage msg) throws TException
	{
		msgTransport_.proto_writeMessageBegin(msg);
		TMessage transformedMsg = new TMessage( RPC_PAIRS.get(msg.name), msg.type, msg.seqid );
		rpcidPairs_.put(msg.seqid, msg.name);
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
	
	public TMessage readMessageBegin() throws TException
	{
		TMessage msg = super.readMessageBegin();
		return new TMessage( fetchRpcName(msg.seqid), msg.type, msg.seqid );
	}
}
