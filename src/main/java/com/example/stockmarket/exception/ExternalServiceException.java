package com.example.stockmarket.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(Throwable throwable) {
        super (throwable);
    }
}
