package com.example.stockmarket.service;

import com.example.stockmarket.service.response.WebCurrencyServiceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class WebCurrencyServiceTest {
    @Value("${currency.service.url}")
    String currencyServiceUrl;
    @Value("${currency.service.key}")
    String currencyKey;
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    WebCurrencyService webCurrencyService;

    @Test
    public void isValidTest() {
        String pair = "USDRUB";
        Map<String, String> response = new HashMap<>();
        response.put(pair, "64.1824");
        String url = currencyServiceUrl + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");
//        webCurrencyServiceResponse.setData();

        when(restTemplate.getForObject(eq(url), any(), anyString(), anyString()))
        .thenReturn(webCurrencyServiceResponse);

//        HttpHeaders httpHeaders = new HttpHeaders();
//        when(restTemplate.headForHeaders(any())).thenReturn(httpHeaders);
//
//        Assertions.assertSame(httpHeaders, restTemplate.headForHeaders(null));

        boolean result = webCurrencyService.isValid(pair);
//
        assertTrue(result);
    }

    @Test
    public void convertTest() {
        String pair = "USDRUB";
        Map<String, String> response = new HashMap<>();
        response.put(pair, "64.1824");
        String url = currencyServiceUrl + "/api/?get=rates&pairs={pair}&key={key}";
        WebCurrencyServiceResponse webCurrencyServiceResponse = new WebCurrencyServiceResponse();
        webCurrencyServiceResponse.setStatus("200");
        webCurrencyServiceResponse.setMessage("rates");

                when(restTemplate.getForObject(url, WebCurrencyServiceResponse.class, pair, currencyKey))
                .thenReturn(webCurrencyServiceResponse);


        double expectResult = Double.parseDouble(new ArrayList<>(webCurrencyServiceResponse.getData().values()).get(0));

        Assertions.assertEquals(expectResult, webCurrencyService.convert("USD", 100, "RUB"));

    }
}