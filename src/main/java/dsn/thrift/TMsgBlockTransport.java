package dsn.thrift;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.TMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class thrift_header {
    public static final int HEADER_LENGTH = 32;
    public static final byte[] HEADER_TYPE = {'t','h','f','t'};
    public int hdr_crc32 = 0;
    public int body_offset = 0;
    public int body_length = 0;
    public int request_hash = 0;
    public int client_timeout = 0;
    public long header_options = 3; //disable forward, enable replication

    public byte[] toByteArray() {
        ByteBuffer bf = ByteBuffer.allocate(HEADER_LENGTH);
        bf.put(HEADER_TYPE);
        bf.putInt(hdr_crc32);
        bf.putInt(body_offset);
        bf.putInt(body_length);
        bf.putInt(request_hash);
        bf.putInt(client_timeout);
        bf.putLong(header_options);
        return bf.array();
    }
};

public class TMsgBlockTransport extends org.apache.thrift.transport.TTransport {
    private static final Log LOG = LogFactory.getLog(TMsgBlockTransport.class);

    private TTransport realTransport_;
    private ByteArrayOutputStream buffer_;
    private boolean isWritingMsg_;
    private thrift_header header_;

    private void writeHeader() throws TTransportException {
        realTransport_.write(header_.toByteArray());
    }

    public TMsgBlockTransport(TTransport transport) {
        realTransport_ = transport;
        buffer_ = new ByteArrayOutputStream();
        isWritingMsg_ = false;
        header_ = new thrift_header();
    }

    @Override
    public void close() {
        realTransport_.close();
    }

    @Override
    public boolean isOpen() {
        return realTransport_.isOpen();
    }

    @Override
    public void open() throws TTransportException {
        realTransport_.open();
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws TTransportException {
        return realTransport_.read(buffer, offset, length);
    }

    @Override
    public void write(byte[] buffer, int offset, int length)
            throws TTransportException {
        buffer_.write(buffer, offset, length);
    }

    public void write(byte[] buffer) throws TTransportException {
        try {
            buffer_.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TTransportException(TTransportException.UNKNOWN, e);
        }
    }

    public void flush() throws TTransportException {
        if (isWritingMsg_) {
            LOG.warn("should not flush when writing a message");
        } else {
            realTransport_.flush();
        }
    }

    public void proto_writeMessageBegin(TMessage msg) {
        isWritingMsg_ = true;
    }

    public void proto_writeMessageEnd(int request_hash) throws TTransportException {
        header_.body_offset = thrift_header.HEADER_LENGTH;
        header_.body_length = buffer_.size();
        header_.request_hash = request_hash;
        writeHeader();

        realTransport_.write(buffer_.toByteArray());
        buffer_.reset();
        isWritingMsg_ = false;
    }
}
