package com.nashss.se.htmvault.exceptions;

public class CloseWorkOrderNotCompleteException extends RuntimeException {
    private static final long serialVersionUID = 6484053031901892689L;

    /**
     * Exception with no message or cause.
     */
    public CloseWorkOrderNotCompleteException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public CloseWorkOrderNotCompleteException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public CloseWorkOrderNotCompleteException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public CloseWorkOrderNotCompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
