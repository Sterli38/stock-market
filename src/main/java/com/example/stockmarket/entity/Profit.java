package com.example.stockmarket.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Profit {
    private String currency;
    private double amountProfit;
}
