package com.nashss.se.htmvault.exceptions;

public class UpdateClosedWorkOrderException extends RuntimeException {
    private static final long serialVersionUID = -3453158469020176782L;

    /**
     * Exception with no message or cause.
     */
    public UpdateClosedWorkOrderException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public UpdateClosedWorkOrderException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public UpdateClosedWorkOrderException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public UpdateClosedWorkOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
