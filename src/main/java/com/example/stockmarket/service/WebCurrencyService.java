package com.example.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WebCurrencyService implements CurrencyService {
    private final RestTemplate restTemplate;
    @Override
    public boolean isValid(String currencyPair) {
        return false;
    }

    @Override
    public double convert(String from, double amount, String in) {
        return 0;
    }
}
