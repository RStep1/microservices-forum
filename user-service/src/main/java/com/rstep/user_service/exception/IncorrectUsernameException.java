package com.rstep.user_service.exception;

public class IncorrectUsernameException extends RuntimeException {
    public IncorrectUsernameException(String errorMessage) {
        super(errorMessage);
    }
}
