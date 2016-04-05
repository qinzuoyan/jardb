package com.xiaomi.infra.pegasus.client;

/**
 * @author qinzuoyan
 */
public class PException extends Exception {

    private static final long serialVersionUID = 4436491238550521203L;

    public PException() {
        super();
    }

    public PException(String message, Throwable cause) {
        super(message, cause);
    }

    public PException(String message) {
        super(message);
    }

    public PException(Throwable cause) {
        super(cause);
    }
}
