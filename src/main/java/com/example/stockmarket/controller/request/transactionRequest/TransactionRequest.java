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
    private Double amount;
    @Positive
    private Long participantId;
    private String receivedCurrency;
    private String givenCurrency;
}
