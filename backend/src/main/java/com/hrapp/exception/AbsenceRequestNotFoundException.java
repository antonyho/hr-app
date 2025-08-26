package com.hrapp.exception;

public class AbsenceRequestNotFoundException extends RuntimeException {
    public AbsenceRequestNotFoundException(String message) {
        super(message);
    }

    public AbsenceRequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}