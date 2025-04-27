package com.rstep1.post_service.exception;

public class NotPostOwnerException extends RuntimeException {
    public NotPostOwnerException(String errorMessage) {
        super(errorMessage);
    }
}