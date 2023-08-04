package com.example.stockmarket.exception;

public abstract class UserException extends IllegalArgumentException {
    public UserException(String message) {
        super(message);
    }
}
