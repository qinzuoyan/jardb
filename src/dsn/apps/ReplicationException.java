package dsn.apps;

public class ReplicationException extends Exception
{
	private static final long serialVersionUID = 4186015142427786503L;

	public enum ErrorCode {
		UNKNOWN, 
		NO_PRIMARY, 
		NO_REPLICA, 
		READ_TABLE_ERROR, 
		NO_META, 
		REPLICATION_ERROR
	};
	public ReplicationException.ErrorCode type = ErrorCode.UNKNOWN;
	public ReplicationException() {
		super();
	}
	public ReplicationException(ReplicationException.ErrorCode t)
	{
		super();
		type = t;
	}
	
	public ReplicationException(ReplicationException.ErrorCode t, String message) {
		super(message);
		type = t;
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