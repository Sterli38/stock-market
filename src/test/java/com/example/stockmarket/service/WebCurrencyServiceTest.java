package com.example.stockmarket.service;

import com.example.stockmarket.config.ApplicationProperties;
import com.example.stockmarket.controller.response.WebCurrencyServiceResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

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
@ExtendWith(MockitoExtension.class)
class WebCurrencyServiceTest {
    @Autowired
    ApplicationProperties applicationProperties;
    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplate originalRestTemplate;
    @Autowired
    private WebCurrencyService webCurrencyService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(webCurrencyService, "restTemplate", restTemplate);
    }

    @AfterEach
    public void after() {
        ReflectionTestUtils.setField(webCurrencyService, "restTemplate", originalRestTemplate);
    }

    @Test
    public void isValidCurrencyPairTest() {
        String currencyPair = "USDRUB";
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";

        Map<String, String> response = new HashMap<>();
        response.put(currencyPair, "64.1824");
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        when(restTemplate.getForObject(eq(url), any(), anyString(), anyString()))
                .thenReturn(webCurrencyServiceResponse);

        boolean result = webCurrencyService.isValidCurrencyPair(currencyPair);

        assertTrue(result);
    }

    @Test
    public void IsValidCurrencyTest() {
        String currency = "EUR";
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=currency_list&key={key}";

        List<String> response = new ArrayList<>();
        response.add("BCHUSD");
        response.add("BCHBCH");
        response.add("BCHEUR");

        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        when(restTemplate.getForObject(eq(url), any(), anyString()))
                .thenReturn(webCurrencyServiceResponse);

        boolean result = webCurrencyService.isValidCurrency(currency);

        assertTrue(result);
    }

    @Test
    public void IsNotValidCurrencyTest() {
        String currency = "BCG";
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=currency_list&key={key}";

        List<String> response = new ArrayList<>();
        response.add("BCHUSD");
        response.add("BCHBCH");
        response.add("BCHEUR");

        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        when(restTemplate.getForObject(eq(url), any(), anyString()))
                .thenReturn(webCurrencyServiceResponse);

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