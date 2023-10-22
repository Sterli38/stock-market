package com.example.stockmarket.service;

public interface CurrencyService {

    boolean isValidCurrencyPair(String currencyPair);

    boolean isValidCurrency(String currency);

    double convert(String from, double amount, String in);
}
