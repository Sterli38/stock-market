package com.example.stockmarket.service;

import com.example.stockmarket.config.ApplicationProperties;
import com.example.stockmarket.controller.response.WebCurrencyServiceResponse;
import com.example.stockmarket.exception.ExternalServiceException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebCurrencyService implements CurrencyService {
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private final MeterRegistry meterRegistry;

    @Override
    public boolean isValidCurrencyPair(String currencyPair) {
        Counter unsuccessfullCounter = meterRegistry.counter("stockMarket.webCurrencyService.unsuccessfulRequests");
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse;
        try {
            Timer timer = meterRegistry.timer("stockMarket.webCurrencyService.requestTime");
            long start = System.currentTimeMillis();
            webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, applicationProperties.getCurrencyServiceKey());
            timer.record(System.currentTimeMillis() - start, TimeUnit.MICROSECONDS);
        } catch (RestClientException exception) {
            unsuccessfullCounter.increment();
            log.error("Error while sending request to WebCurrencyService", exception);
            throw new ExternalServiceException(exception);
        }
        if (webCurrencyServiceResponse == null) {
            unsuccessfullCounter.increment();
            throw new RuntimeException("answer from Currency service was not received");
        }
        meterRegistry.counter("stockMarket.webCurrencyService.successfulRequest").increment();
        return "200".equals(webCurrencyServiceResponse.getStatus());
    }

    @Override
    public boolean isValidCurrency(String currency) {
        Counter unsuccessfullCounter = meterRegistry.counter("stockMarket.webCurrencyService.unsuccessfulRequest");
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=currency_list&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse;
        try {
            Timer timer = meterRegistry.timer("stockMarket.webCurrencyService.requestTime");
            long start = System.currentTimeMillis();
            webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, applicationProperties.getCurrencyServiceKey());
            timer.record(System.currentTimeMillis() - start, TimeUnit.MICROSECONDS);
        } catch (RestClientException exception) {
            unsuccessfullCounter.increment();
            log.error("Error while sending request to WebCurrencyService", exception);
            throw new ExternalServiceException(exception);
        }
        if (webCurrencyServiceResponse == null) {
            unsuccessfullCounter.increment();
            throw new RuntimeException("answer from Currency service was not received");
        }
        meterRegistry.counter("stockMarket.webCurrencyService.successfulRequest").increment();
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
        Timer timer = meterRegistry.timer("stockMarket.webCurrencyService.requestTime");
        long start = System.currentTimeMillis();
        WebCurrencyServiceResponse webCurrencyServiceResponse = restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, applicationProperties.getCurrencyServiceKey());
        timer.record(System.currentTimeMillis() - start, TimeUnit.MICROSECONDS);
        String rate = new ArrayList<>(webCurrencyServiceResponse.getDataAsMap().values()).get(0);
        return Double.parseDouble(rate) * amount;
    }
}
