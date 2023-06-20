package com.nashss.se.htmvault.exceptions;

/**
 * An exception used when a work order specified by work order id is not found in the database.
 */
public class WorkOrderNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -3593057595469138155L;

    /**
     * Exception with no message or cause.
     */
    public WorkOrderNotFoundException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public WorkOrderNotFoundException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public WorkOrderNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public WorkOrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
