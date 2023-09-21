package com.example.stockmarket.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "Сущность транзакции")
public class Transaction {
    @Schema(description = "Идентификатор транзакции")
    private Long id;
    @Schema(description = "Тип операции")
    private OperationType operationType;
    @Schema(description = "Дата операции")
    private Date date;
    @Schema(description = "Полученная валюта")
    private String receivedCurrency;
    @Schema(description = "Количество полученной валюты")
    private Double receivedAmount;
    @Schema(description = "Отданная валюта")
    private String givenCurrency;
    @Schema(description = "Количество отданной валюты")
    private Double givenAmount;
    @Schema(description = "Участник который выполнял транзакцию")
    private Participant participant;
    @Schema(description = "Комиссия биржи за транзакцию")
    private double commission;
}
