package com.example.stockmarket.controller.request.transactionRequest;

import com.example.stockmarket.entity.OperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "Сущность запроса для получения транзакций пользователя по фильтру")
@Data
public class GetTransactionsRequest {
    @Schema(description = "Идентификатор участника")
    @NotNull
    private Long participantId;
    @Schema(description = "Тип операции")
    private OperationType operationType;
    @Schema(description = "Начало временного отрезка когда производилась транзакция")
    private Date after;
    @Schema(description = "Конец временного отрезка")
    private Date before;
    @Schema(description = "Минимальное количество полученной валюты")
    private Double receivedMinAmount;
    @Schema(description = "Максимальное количество полученной валюты")
    private Double receivedMaxAmount;
    @Schema(description = "Минимальное количество отданной валюты")
    private Double givenMinAmount;
    @Schema(description = "Максимальное количество отданной валюты")
    private Double givenMaxAmount;
    @Schema(description = "Список отданных валют")
    private List<String> givenCurrencies;
    @Schema(description = "Список полученных валют")
    private List<String> receivedCurrencies;
}
