package com.nashss.se.htmvault.exceptions;

/**
 * An exception used when attempting to update a device record for a device that is an
 * inactive/retired status.
 */
public class UpdateRetiredDeviceException extends RuntimeException {
    private static final long serialVersionUID = 8793902683850361008L;

    /**
     * Exception with no message or cause.
     */
    public UpdateRetiredDeviceException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public UpdateRetiredDeviceException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public UpdateRetiredDeviceException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public UpdateRetiredDeviceException(String message, Throwable cause) {
        super(message, cause);
    }
}
