package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {
    @Positive
    private Long participantId;
}
