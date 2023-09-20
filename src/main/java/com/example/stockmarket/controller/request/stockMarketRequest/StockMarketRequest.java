package com.example.stockmarket.controller.request.stockMarketRequest;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Date;

@Data
public class StockMarketRequest {
    private String currency;
    private Date after;
    private Date before;
}
