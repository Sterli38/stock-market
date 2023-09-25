package com.example.stockmarket.controller.request.transactionRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Сущность запроса для получения баланса по валюте")
@EqualsAndHashCode(callSuper = true)
@Data
public class GetBalanceRequest extends AbstractRequest {
    @Schema(description = "Валюта в которой получаем баланс")
    @NotNull
    private String currency;
}
