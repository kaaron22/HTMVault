package com.nashss.se.htmvault.exceptions;

public class ManufacturerModelNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 7057812302176365609L;

    public ManufacturerModelNotFoundException() {
        super();
    }

    public ManufacturerModelNotFoundException(String message) {
        super(message);
    }

    public ManufacturerModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManufacturerModelNotFoundException(Throwable cause) {
        super(cause);
    }
}
