package dsn.operator;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

public abstract class client_operator {
  public abstract void send(TServiceClient client) throws TException;
  public abstract void recv_data(org.apache.thrift.protocol.TProtocol iprot) throws TException;
  public abstract dsn.base.error_code get_result_error();
  public abstract dsn.replication.global_partition_id get_op_gpid();
  public Object notifier;
};
