package com.example.stockmarket.exception;

public class NoAccessToPerformOperation extends UserException {

    public NoAccessToPerformOperation(String message) {
        super("Insufficient rights to " + message);
    }
}
