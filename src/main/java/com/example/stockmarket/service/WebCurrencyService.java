package com.example.stockmarket.service;

import com.example.stockmarket.service.response.WebCurrencyServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WebCurrencyService implements CurrencyService {
    private final RestTemplate restTemplate;

    @Value("${currency.service.url}")
    private final String currencyServiceUrl;
    @Value("${currency.service.key}")
    private final String currencyServiceKey;

    @Override
    public boolean isValid(String currencyPair) {
        String url = currencyServiceUrl + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, currencyServiceKey);
        if(webCurrencyServiceResponse == null) {
            throw new RuntimeException("answer from Currency service was not received");
        }
        if(webCurrencyServiceResponse.getStatus().equals("500")) {
            return false;
        } else if (webCurrencyServiceResponse.getStatus().equals("200")) {
            return true;
        }
        return false;
    }

    @Override
    public double convert(String from, double amount, String in) {
        return 0;
    }
}
