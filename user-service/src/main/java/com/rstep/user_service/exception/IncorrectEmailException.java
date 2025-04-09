package com.rstep.user_service.exception;

public class IncorrectEmailException extends RuntimeException {
    public IncorrectEmailException(String errorMessage) {
        super(errorMessage);
    }
}
