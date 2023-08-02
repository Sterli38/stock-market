package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MakeExchangeRequest extends TransactionRequest {
    @NotNull
    private String requiredCurrency;
}
