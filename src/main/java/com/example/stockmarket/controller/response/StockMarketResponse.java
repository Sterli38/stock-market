package com.example.stockmarket.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Сущность ответа для функционала биржи")
@Data
public class StockMarketResponse {
    @Schema(description = "Валюта")
    private String currency;
    @Schema(description = "Количество прибыли в валюте")
    private double amountProfit;
}
