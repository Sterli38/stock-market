package com.example.stockmarket.controller.response;

import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

import java.util.Date;

@Schema(description = "Сущность ответа для операций с транзакциями")
@Data
public class TransactionResponse {
    @Schema(description = "Идентификатор транзакции")
    private Long id;
    @Schema(description = "Тип операции транзакции")
    private OperationType operationType;
    @Schema(description = "Дата выполнения")
    private Date date;
    @Schema(description = "Полученная валюта")
    private String receivedCurrency;
    @Schema(description = "Количество полученной валюты")
    private Double receivedAmount;
    @Schema(description = "Отданная валюта")
    private String givenCurrency;
    @Schema(description = "Количество отданной валюты")
    private Double givenAmount;
    @Schema(description = "Участник который проводил транзакцию")
    private Participant participant;
    @Schema(description = "Комиссия биржы")
    private Double commission;
}
