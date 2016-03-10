package dsn.operator;

import org.apache.thrift.TException;

import dsn.apps.rrdb;
import dsn.apps.update_request;
import dsn.replication.write_request_header;

public class rrdb_merge_operator extends rrdb_write_operator {
	public rrdb_merge_operator(write_request_header header, update_request request) {
		super(header);
		this.request = request;
	}
	public void client_execute(rrdb.Client client) throws TException {
		resp = client.merge(header, request);
	}
	private update_request request;
}