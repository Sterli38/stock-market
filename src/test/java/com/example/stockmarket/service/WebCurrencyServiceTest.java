package com.example.stockmarket.service;

import com.example.stockmarket.config.ApplicationProperties;
import com.example.stockmarket.controller.response.WebCurrencyServiceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class WebCurrencyServiceTest extends TestWithWebCurrency {
    @Test
    public void isValidCurrencyPairTest() {
        String currencyPair = "USDRUB";

        setExpectedWebCurrencyServiceResponseForCurrencyRate("USDRUB", "64.1824");

        boolean result = webCurrencyService.isValidCurrencyPair(currencyPair);

        assertTrue(result);
    }

    @Test
    public void IsValidCurrencyTest() {
        String currency = "EUR";

        setExpectedWebCurrencyServiceResponseForAvailableCurrencies();

        boolean result = webCurrencyService.isValidCurrency(currency);

        assertTrue(result);
    }

    @Test
    public void IsNotValidCurrencyTest() {
        String currency = "BCG";

        setExpectedWebCurrencyServiceResponseForAvailableCurrencies();

        boolean result = webCurrencyService.isValidCurrency(currency);

        assertFalse(result);
    }

    @Test
    public void convertTest() {
        String currencyPair = "USDRUB";
        double amount = 100;
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";

        Map<String, String> response = new HashMap<>();
        response.put(currencyPair, "64.1824");
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        double expectResult = Double.parseDouble(new ArrayList<>(webCurrencyServiceResponse.getDataAsMap().values()).get(0)) * amount;

        when(restTemplate.getForObject(url, WebCurrencyServiceResponse.class, currencyPair, applicationProperties.getCurrencyServiceKey()))
                .thenReturn(webCurrencyServiceResponse);

        Assertions.assertEquals(expectResult, webCurrencyService.convert("USD", 100, "RUB"));
    }
}