package com.example.stockmarket.service;

import com.example.stockmarket.config.ApplicationProperties;
import com.example.stockmarket.controller.response.WebCurrencyServiceResponse;
import com.example.stockmarket.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebCurrencyService implements CurrencyService {
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValidCurrencyPair(String currencyPair) {
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse;
        try {
            webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, applicationProperties.getCurrencyServiceKey());
        } catch (RestClientException exception) {
            log.error("Error while sending request to WebCurrencyService", exception);
            throw new ExternalServiceException(exception);
        }
        if (webCurrencyServiceResponse == null) {
            throw new RuntimeException("answer from Currency service was not received");
        }
        return "200".equals(webCurrencyServiceResponse.getStatus());
    }

    @Override
    public boolean isValidCurrency(String currency) {
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=currency_list&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse;
        try {
            webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, applicationProperties.getCurrencyServiceKey());
        } catch (RestClientException exception) {
            log.error("Error while sending request to WebCurrencyService", exception);
            throw new ExternalServiceException(exception);
        }
        if (webCurrencyServiceResponse == null) {
            throw new RuntimeException("answer from Currency service was not received");
        }

        List<String> list = webCurrencyServiceResponse.getDataAsList();

        if (currency.length() != 3) {
            return false;
        }
        for (String value : list) {
            if (value.contains(currency)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double convert(String from, double amount, String in) {
        String currencyPair = from + in;
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, applicationProperties.getCurrencyServiceKey());
        String rate = new ArrayList<>(webCurrencyServiceResponse.getDataAsMap().values()).get(0);
        return Double.parseDouble(rate) * amount;
    }
}
