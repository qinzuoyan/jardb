package dsn.operator;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import dsn.apps.rrdb;
import dsn.thrift.TMsgBlockProtocol;

public abstract class rrdb_operator extends client_operator
{
  private void set_request_hash(rrdb.Client client, dsn.replication.global_partition_id gpid)
  {
    ((TMsgBlockProtocol)client.getOutputProtocol()).setRequestHash( gpid.app_id^gpid.pidx );
  }
  public final void send(TServiceClient client) throws TException
  {
    rrdb.Client c = (rrdb.Client)(client);
    set_request_hash(c, get_op_gpid());
    client_send(c);
  }
  public abstract void client_send(rrdb.Client client) throws TException;
  public abstract dsn.base.error_code get_result_error();
  public abstract dsn.replication.global_partition_id get_op_gpid();
}
