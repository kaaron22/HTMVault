package com.nashss.se.htmvault.exceptions;

public class DevicePreviouslyAddedException extends RuntimeException {

    private static final long serialVersionUID = 7555733438817388724L;

    public DevicePreviouslyAddedException() {
        super();
    }

    public DevicePreviouslyAddedException(String message) {
        super(message);
    }

    public DevicePreviouslyAddedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DevicePreviouslyAddedException(Throwable cause) {
        super(cause);
    }
}
