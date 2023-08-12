package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class AbstractRequest {
    @Positive
    private Long participantId;
}
