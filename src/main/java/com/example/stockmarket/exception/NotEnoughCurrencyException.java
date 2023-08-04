package com.example.stockmarket.exception;

public class NotEnoughCurrencyException extends UserException {
    public NotEnoughCurrencyException(String currency) {
        super("not enough currency in : " + currency);
    }
}
