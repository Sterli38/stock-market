package com.example.stockmarket.exception;

public class CurrencyPairIsNotValidException extends IllegalArgumentException {
    public CurrencyPairIsNotValidException(String invalidCurrencyPair) {
        super("Invalid currency pair: " + invalidCurrencyPair);
    }
}
