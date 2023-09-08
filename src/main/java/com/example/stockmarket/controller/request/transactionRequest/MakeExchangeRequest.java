package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MakeExchangeRequest extends AbstractRequest {
    @NotBlank
    private String receivedCurrency;
    @NotNull
    private Double givenAmount;
    @NotBlank
    private String givenCurrency;
}
