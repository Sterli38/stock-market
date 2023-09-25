package com.example.stockmarket.controller.request.transactionRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Сущность запроса для вывода")
@EqualsAndHashCode(callSuper = true)
@Data
public class MakeWithdrawalRequest extends AbstractRequest {
    @Schema(description = "Валюта для вывода")
    @NotNull
    private String givenCurrency;
    @Schema(description = "Количество выводимой валюты")
    @NotNull
    private Double givenAmount;
}
