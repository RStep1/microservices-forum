package com.rstep1.user_service.exception;

public class IncorrectEmailException extends RuntimeException {
    public IncorrectEmailException(String errorMessage) {
        super(errorMessage);
    }
}
