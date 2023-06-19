package com.nashss.se.htmvault.exceptions;

public class ManufacturerModelNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 7057812302176365609L;

    /**
     * Exception with no message or cause.
     */
    public ManufacturerModelNotFoundException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public ManufacturerModelNotFoundException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public ManufacturerModelNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public ManufacturerModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }



}
