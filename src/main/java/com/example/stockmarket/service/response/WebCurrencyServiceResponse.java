package com.example.stockmarket.service.response;

import lombok.Value;
import java.util.List;
import java.util.Map;

@Value
public class WebCurrencyServiceResponse {
    private final String status;
    private final String message;
    private final Map<String, String> data;
}
