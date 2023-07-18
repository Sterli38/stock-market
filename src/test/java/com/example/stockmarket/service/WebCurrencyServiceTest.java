package com.example.stockmarket.service;

import com.example.stockmarket.config.ApplicationProperties;
import com.example.stockmarket.controller.response.WebCurrencyServiceResponse;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class WebCurrencyServiceTest {
    @Autowired
    ApplicationProperties properties;
    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private WebCurrencyService webCurrencyService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(webCurrencyService, "restTemplate", restTemplate);
    }

    @Test
    public void isValidTest() {
        String pair = "USDRUB";
        String url = properties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";

        Map<String, String> response = new HashMap<>();
        response.put(pair, "64.1824");
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        when(restTemplate.getForObject(eq(url), any(), anyString(), anyString()))
                .thenReturn(webCurrencyServiceResponse);

        boolean result = webCurrencyService.isValid(pair);

        assertTrue(result);
    }

    @Test
    public void convertTest() {
        String pair = "USDRUB";
        double amount = 100;
        String url = properties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";

        Map<String, String> response = new HashMap<>();
        response.put(pair, "64.1824");
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        double expectResult = Double.parseDouble(new ArrayList<>(webCurrencyServiceResponse.getData().values()).get(0)) * amount;

        when(restTemplate.getForObject(url, WebCurrencyServiceResponse.class, pair, properties.getCurrencyServiceKey()))
                .thenReturn(webCurrencyServiceResponse);

        Assertions.assertEquals(expectResult, webCurrencyService.convert("USD", 100, "RUB"));
    }
}