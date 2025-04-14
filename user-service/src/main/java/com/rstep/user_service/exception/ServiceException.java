package com.rstep.user_service.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}