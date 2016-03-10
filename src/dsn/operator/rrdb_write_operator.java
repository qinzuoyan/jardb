package dsn.operator;

import org.apache.thrift.TException;

import dsn.apps.replication_write_response;
import dsn.apps.rrdb;
import dsn.base.error_code;

public class rrdb_write_operator extends rrdb_operator {
	@Override
	public void client_execute(rrdb.Client client) throws TException {}

	@Override
	public error_code get_result_error() {
		return resp.ec;
	}
	
	@Override
	public dsn.replication.global_partition_id get_op_gpid() {
		return header.gpid;
	}
	
	public replication_write_response get_response() {
		return resp;
	}
	
	public rrdb_write_operator(dsn.replication.write_request_header header) {
		this.header = header;
	}
	protected dsn.replication.write_request_header header;
	protected replication_write_response resp;
};
