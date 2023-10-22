package com.example.stockmarket.exception;

public class CurrencyIsNotValidException extends UserException {
    public CurrencyIsNotValidException(String currency) {
        super("Currency " + currency + " is not valid");
    }
}
