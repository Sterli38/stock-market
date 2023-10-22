package com.example.stockmarket.controller.response;

import lombok.Data;

import java.util.Map;

@Data
public class WebCurrencyServiceResponseForMap extends WebCurrencyServiceResponse {
    private Map<String, String> data;
}
