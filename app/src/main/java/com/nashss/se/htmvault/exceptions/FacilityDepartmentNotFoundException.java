package com.nashss.se.htmvault.exceptions;

/**
 * An exception class to use when a facility and assigned department which does not exist is selected
 * while adding or updating a device record.
 */
public class FacilityDepartmentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6387140391932125423L;

    /**
     * Exception with no message or cause.
     */
    public FacilityDepartmentNotFoundException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public FacilityDepartmentNotFoundException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public FacilityDepartmentNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public FacilityDepartmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
