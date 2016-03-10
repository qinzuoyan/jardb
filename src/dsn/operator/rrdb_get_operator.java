package dsn.operator;

import org.apache.thrift.TException;

import dsn.apps.replication_read_response;
import dsn.apps.rrdb;
import dsn.base.blob;
import dsn.base.error_code;
import dsn.replication.global_partition_id;
import dsn.replication.read_request_header;

public class rrdb_get_operator extends rrdb_operator {
	public rrdb_get_operator(read_request_header header, blob request) {
		this.header = header;
		this.request = request;
	}
	public void client_execute(rrdb.Client client) throws TException {
		resp = client.get(header, request);
	}
	public error_code get_result_error() {
		return resp.ec;
	}
	public global_partition_id get_op_gpid() {
		return header.gpid;
	}
	
	public replication_read_response get_response() {
		return resp;
	}
	private read_request_header header;
	private blob request;
	private replication_read_response resp;
}