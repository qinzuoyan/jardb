package dsn.apps;

import dsn.base.error_code;

public class ReplicationException extends Exception
{
  private static final long serialVersionUID = 4186015142427786503L;

  public error_code.error_types err_type;
  public ReplicationException() {
    super();
  }
  public ReplicationException(error_code.error_types t)
  {
    super();
    err_type = t;
  }
  
  public ReplicationException(error_code.error_types t, String message) {
    super(message);
    err_type = t;
  }
  
  public ReplicationException(Throwable cause)
  {
    super(cause);
  }
  
  public ReplicationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}