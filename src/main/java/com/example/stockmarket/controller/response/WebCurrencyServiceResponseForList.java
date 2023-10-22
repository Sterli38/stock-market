package com.example.stockmarket.controller.response;

import lombok.Data;

import java.util.List;

@Data
public class WebCurrencyServiceResponseForList extends WebCurrencyServiceResponse {
    private List<String> data;
}
