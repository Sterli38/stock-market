package com.example.stockmarket.controller.request.transactionRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Сущность запроса для пополнения пользовательского счёта")
@EqualsAndHashCode(callSuper = true)
@Data
public class MakeDepositingRequest extends AbstractRequest {
    @Schema(description = "Валюта для пополнения")
    @NotNull
    private String receivedCurrency;
    @Schema(description = "Количество пополнения ")
    @NotNull
    private Double receivedAmount;
}
