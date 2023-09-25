package com.example.stockmarket.controller.request.transactionRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Сущность запроса для произведения обмена")
@EqualsAndHashCode(callSuper = true)
@Data
public class MakeExchangeRequest extends AbstractRequest {
    @Schema(description = "Валюта которую получаем")
    @NotBlank
    private String receivedCurrency;
    @Schema(description = "Количество валюты которую отдаём")
    @NotNull
    private Double givenAmount;
    @Schema(description = "Валюта которую отдаём")
    @NotBlank
    private String givenCurrency;
}
