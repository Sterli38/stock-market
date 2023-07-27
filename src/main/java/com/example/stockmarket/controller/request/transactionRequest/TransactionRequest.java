package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Date;

@Data
public class TransactionRequest {
    @NotNull
    private Date date;
    @NotNull
    private double amount;
    @NotNull
    @Positive
    private Long participantId;
    @Nullable
    private String receivedCurrency;
    @Nullable
    private String givenCurrency;
}
