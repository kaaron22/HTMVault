package com.nashss.se.htmvault.exceptions;

public class FacilityDepartmentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6387140391932125423L;

    public FacilityDepartmentNotFoundException() {
        super();
    }

    public FacilityDepartmentNotFoundException(String message) {
        super(message);
    }

    public FacilityDepartmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FacilityDepartmentNotFoundException(Throwable cause) {
        super(cause);
    }
}
