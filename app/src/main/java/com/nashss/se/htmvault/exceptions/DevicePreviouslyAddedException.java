package com.nashss.se.htmvault.exceptions;

public class DevicePreviouslyAddedException extends RuntimeException {

    private static final long serialVersionUID = 7555733438817388724L;

    /**
     * Exception with no message or cause.
     */
    public DevicePreviouslyAddedException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public DevicePreviouslyAddedException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public DevicePreviouslyAddedException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public DevicePreviouslyAddedException(String message, Throwable cause) {
        super(message, cause);
    }
}
