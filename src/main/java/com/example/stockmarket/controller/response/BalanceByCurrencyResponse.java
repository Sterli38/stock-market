package com.example.stockmarket.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Сущность ответа для операции получение баланса по валюте")
@Data
public class BalanceByCurrencyResponse {
    @Schema(description = "Баланс валюты")
    private double currencyBalance;
}
