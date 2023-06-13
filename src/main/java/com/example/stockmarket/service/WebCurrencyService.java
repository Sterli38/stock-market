package com.example.stockmarket.service;

import com.example.stockmarket.service.response.WebCurrencyServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebCurrencyService implements CurrencyService {
    private final RestTemplate restTemplate;

    @Value("${currency.service.url}")
    private String currencyServiceUrl;
    @Value("${currency.service.key}")
    private String currencyServiceKey;

    @Override
    public boolean isValid(String currencyPair) {
        String url = currencyServiceUrl + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, currencyServiceKey);
        if (webCurrencyServiceResponse == null) {
            throw new RuntimeException("answer from Currency service was not received");
        }
        if ("500".equals(webCurrencyServiceResponse.getStatus())) {
            return false;
        } else {
            return "200".equals(webCurrencyServiceResponse.getStatus());
        }
    }
    @Override
    public double convert(String from, double amount, String in) {
        String pair = from + in;
        String url = currencyServiceUrl + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, pair, currencyServiceKey);
        String rate = new ArrayList<>(webCurrencyServiceResponse.getData().values()).get(0);
        return Double.parseDouble(rate) * amount;
    }
}
