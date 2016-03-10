package dsn.operator;

import org.apache.thrift.TException;

import dsn.apps.rrdb;
import dsn.base.blob;
import dsn.replication.write_request_header;

public class rrdb_remove_operator extends rrdb_write_operator {
	public rrdb_remove_operator(write_request_header header, blob request) {
		super(header);
		this.request = request;
	}
	public void client_execute(rrdb.Client client) throws TException {
		resp = client.remove(header, request);
	}
	private blob request;
}
