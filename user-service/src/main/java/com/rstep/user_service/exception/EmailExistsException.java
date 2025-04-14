package com.rstep.user_service.exception;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String errorMessage) {
        super(errorMessage);
    }
}
