package com.nashss.se.htmvault.exceptions;

/**
 * An exception used when an attempt is made to retire a device that has any work orders remaining
 * in an open (incomplete) status.
 */
public class RetireDeviceWithOpenWorkOrdersException extends RuntimeException {
    private static final long serialVersionUID = 3912372867250903276L;

    /**
     * Exception with no message or cause.
     */
    public RetireDeviceWithOpenWorkOrdersException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public RetireDeviceWithOpenWorkOrdersException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public RetireDeviceWithOpenWorkOrdersException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public RetireDeviceWithOpenWorkOrdersException(String message, Throwable cause) {
        super(message, cause);
    }
}
