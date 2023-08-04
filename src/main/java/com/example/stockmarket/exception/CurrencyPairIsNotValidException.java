package com.example.stockmarket.exception;

public class CurrencyPairIsNotValidException extends UserException {
    public CurrencyPairIsNotValidException(String invalidCurrencyPair) {
        super("Invalid currency pair: " + invalidCurrencyPair);
    }
}
