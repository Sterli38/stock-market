package com.example.stockmarket.service;

public interface CurrencyService {

    boolean isValid(String currencyPair);
    double convert(String from, double amount, String in);
}
