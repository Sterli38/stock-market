package com.example.stockmarket.exception;

public class NoCurrencyForAmountException extends UserException{
    public NoCurrencyForAmountException() {
        super("No currency entered for the amount");
    }
}
