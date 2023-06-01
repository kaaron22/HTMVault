package com.nashss.se.htmvault.exceptions;

public class DeviceWithControlNumberAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1184569568140578969L;

    public DeviceWithControlNumberAlreadyExistsException() {
        super();
    }

    public DeviceWithControlNumberAlreadyExistsException(String message) {
        super(message);
    }

    public DeviceWithControlNumberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceWithControlNumberAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
