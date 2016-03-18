package dsn.thrift;

import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.TMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class thrift_header 
{ 
  public static final int TYPE = 0x1234abcd;
  public static final int HEADER_LENGTH = 28;
      
  public int hdr_type = TYPE;
  public int hdr_crc32 = 0;
  public int total_length;
  public int body_offset;
  public int request_hash;
  //disable forward, enable replication
  public long options = 3;
};

public class TMsgBlockTransport extends org.apache.thrift.transport.TTransport {
  private TTransport realTransport_;
  private ByteArrayOutputStream buffer_;
  private boolean isWritingMsg_;
  private thrift_header header_;
  private final byte[] inoutTemp = new byte[8];
  
  private void writeRawI32(int i32) throws TTransportException
  {
      inoutTemp[0] = (byte)(0xff & (i32 >> 24));
      inoutTemp[1] = (byte)(0xff & (i32 >> 16));
      inoutTemp[2] = (byte)(0xff & (i32 >> 8));
      inoutTemp[3] = (byte)(0xff & (i32));
      realTransport_.write(inoutTemp, 0, 4);
  }
  
  private void writeRawI64(long i64) throws TTransportException
  {
      inoutTemp[0] = (byte)(0xff & (i64 >> 56));
      inoutTemp[1] = (byte)(0xff & (i64 >> 48));
      inoutTemp[2] = (byte)(0xff & (i64 >> 40));
      inoutTemp[3] = (byte)(0xff & (i64 >> 32));
      inoutTemp[4] = (byte)(0xff & (i64 >> 24));
      inoutTemp[5] = (byte)(0xff & (i64 >> 16));
      inoutTemp[6] = (byte)(0xff & (i64 >> 8));
      inoutTemp[7] = (byte)(0xff & (i64));
      realTransport_.write(inoutTemp, 0, 8);
  }
  
  private void writeHeader() throws TTransportException 
  {
    writeRawI32(thrift_header.TYPE);
    writeRawI32(0);
    writeRawI32(header_.total_length);
    writeRawI32(header_.body_offset);
    writeRawI32(header_.request_hash);
    writeRawI64(header_.options);
  }
  
  public TMsgBlockTransport(TTransport transport)
  {
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
      throws TTransportException 
  {
    buffer_.write(buffer, offset, length);
  }
  
  public void write(byte[] buffer) throws TTransportException
  {
    try 
    {
      buffer_.write(buffer);
    } 
    catch (IOException e) 
    {
      e.printStackTrace();
      throw new TTransportException(TTransportException.UNKNOWN, e);
    }
  }
  
  public void flush() throws TTransportException
  {
    if (isWritingMsg_)
    {
      System.out.println("don't do flush when writing a message");
    }
    else
    {
      realTransport_.flush();
    }
  }
  
  public void proto_writeMessageBegin(TMessage msg)
  {
    isWritingMsg_ = true;
  }
  
  public void proto_writeMessageEnd(int request_hash) throws TTransportException
  {
    header_.total_length = thrift_header.HEADER_LENGTH + buffer_.size();
    header_.body_offset = thrift_header.HEADER_LENGTH;
    header_.request_hash = request_hash;
    writeHeader();
    
    realTransport_.write(buffer_.toByteArray());
    buffer_.reset();
    isWritingMsg_ = false;
  }
}
