package com.example.stockmarket.controller.request.transactionRequest;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class BalanceRequest {
    @Positive
    private Long participantId;
    @NotNull
    private String givenCurrency;
}
