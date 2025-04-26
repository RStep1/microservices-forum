package com.rstep1.user_service.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
