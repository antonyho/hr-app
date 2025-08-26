package com.hrapp.exception;

public class InvalidRequestStatusException extends RuntimeException {
    public InvalidRequestStatusException(String message) {
        super(message);
    }

    public InvalidRequestStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}