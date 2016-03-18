package dsn.operator;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import dsn.apps.rrdb;
import dsn.apps.update_request;
import dsn.replication.write_request_header;

public class rrdb_put_operator extends rrdb_write_operator {
  public rrdb_put_operator(write_request_header header, update_request request) {
    super(header);
    this.request = request;
  }
  
  public void client_send(rrdb.Client client) throws TException {
    client.send_put(header, request);
  }
  
  public void recv_data(TProtocol iprot) throws TException {
    rrdb.put_result result = new rrdb.put_result();
    result.read(iprot);
    if (result.isSetSuccess())
      resp = result.success;
    else
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "put failed: unknown result");    
  }

  private update_request request;
}