package com.example.stockmarket.service.response;

import lombok.Value;
import java.util.List;

@Value
public class WebCurrencyServiceResponse {
    private final String status;
    private final String message;
    private final List<String> data;
}
