package dsn.operator;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import dsn.apps.rrdb;
import dsn.base.blob;
import dsn.replication.write_request_header;

public class rrdb_remove_operator extends rrdb_write_operator {
    public rrdb_remove_operator(write_request_header header, blob request) {
        super(header);
        this.request = request;
    }

    public void client_send(rrdb.Client client) throws TException {
        client.send_remove(header, request);
    }

    public void recv_data(TProtocol iprot) throws TException {
        rrdb.put_result result = new rrdb.put_result();
        result.read(iprot);
        if (result.isSetSuccess())
            resp = result.success;
        else
            throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "put failed: unknown result");
    }

    private blob request;
}
