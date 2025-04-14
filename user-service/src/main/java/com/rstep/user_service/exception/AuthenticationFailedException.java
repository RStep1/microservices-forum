package com.rstep.user_service.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String errorMessage) {
        super(errorMessage);
    }
}
