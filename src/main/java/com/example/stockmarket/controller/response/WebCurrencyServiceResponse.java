package com.example.stockmarket.controller.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WebCurrencyServiceResponse {
    private String status;
    private String message;
    private Object data;

    public Map<String, String> getDataAsMap() {
        if(isDataMap()) {
            return (Map<String, String>) data;
        }
        throw new IllegalArgumentException();
    }

    public List<String> getDataAsList() {
        if(isDataList()) {
            return (List<String>) data;
        }
        throw new IllegalArgumentException();
    }

    public boolean isDataList() {
        return data instanceof List;
    }

    public boolean isDataMap() {
        return data instanceof Map;
    }
}
