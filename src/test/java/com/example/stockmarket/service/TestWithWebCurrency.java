package com.example.stockmarket.service;

import com.example.stockmarket.config.ApplicationProperties;
import com.example.stockmarket.controller.response.WebCurrencyServiceResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TestWithWebCurrency {
    @Autowired
    public ApplicationProperties applicationProperties;
    @Mock
    public RestTemplate restTemplate;
    @Autowired
    public RestTemplate originalRestTemplate;
    @Autowired
    public WebCurrencyService webCurrencyService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(webCurrencyService, "restTemplate", restTemplate);
    }

    @AfterEach
    public void after() {
        ReflectionTestUtils.setField(webCurrencyService, "restTemplate", originalRestTemplate);}

    public void setExpectedWebCurrencyServiceResponseForAvailableCurrencies() {
        String url = applicationProperties.getCurrencyServiceUrl() + "/api/?get=currency_list&key={key}";

        List<String> response = new ArrayList<>();
        response.add("BCHUSD");
        response.add("RUBBCH");
        response.add("BCHEUR");

        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
        webCurrencyServiceResponse.setData(response);

        when(restTemplate.getForObject(eq(url), any(), anyString()))
                .thenReturn(webCurrencyServiceResponse);

    }

    public void setExpectedWebCurrencyServiceResponseForCurrencyRate(String responseMapKey, String responseMapValue) {
        String url1 = applicationProperties.getCurrencyServiceUrl() + "/api/?get=rates&pairs={pair}&key={key}";

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(responseMapKey, responseMapValue);

        WebCurrencyServiceResponse webCurrencyServiceResponse1 = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse1.setStatus("200");
        webCurrencyServiceResponse1.setMessage("rates");
        webCurrencyServiceResponse1.setData(responseMap);

        when(restTemplate.getForObject(eq(url1), any(), anyString(), anyString()))
                .thenReturn(webCurrencyServiceResponse1);
    }
}
