package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {
    @Positive
    private Long participantId;
    @NotNull
    private String givenCurrency;
    @NotNull
    private Double givenAmount;
}
