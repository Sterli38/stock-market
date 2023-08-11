package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MakeDepositingRequest extends TransactionRequest {
    @NotNull
    private String receivedCurrency;
    @NotNull
    private Double receivedAmount;
}
