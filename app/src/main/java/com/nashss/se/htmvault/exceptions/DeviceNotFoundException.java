package com.nashss.se.htmvault.exceptions;

public class DeviceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -844696552853752078L;

    /**
     * Exception with no message or cause.
     */
    public DeviceNotFoundException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public DeviceNotFoundException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public DeviceNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public DeviceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
