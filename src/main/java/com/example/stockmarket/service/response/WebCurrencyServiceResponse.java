package com.example.stockmarket.service.response;

import lombok.Data;
import lombok.Value;
import java.util.List;
import java.util.Map;

@Data
public class WebCurrencyServiceResponse {
    private String status;
    private String message;
    private Map<String, String> data;
}
