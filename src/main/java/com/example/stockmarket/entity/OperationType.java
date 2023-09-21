package com.example.stockmarket.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип операции совершённой участником")
public enum OperationType {
    DEPOSITING,
    EXCHANGE,
    WITHDRAWAL
}
