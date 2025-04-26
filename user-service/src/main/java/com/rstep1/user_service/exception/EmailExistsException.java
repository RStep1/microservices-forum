package com.rstep1.user_service.exception;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String errorMessage) {
        super(errorMessage);
    }
}
