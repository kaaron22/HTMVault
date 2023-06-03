package com.nashss.se.htmvault.exceptions;

public class DeviceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -844696552853752078L;

    public DeviceNotFoundException() {
        super();
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceNotFoundException(Throwable cause) {
        super(cause);
    }
}
