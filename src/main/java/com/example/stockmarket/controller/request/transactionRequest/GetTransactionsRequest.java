package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class GetTransactionsRequest {
    @NotNull
    private Long participantId;
    private String operationType;
    private Date after;
    private Date before;
    private Double minAmount;
    private Double maxAmount;
    private String currency;
}
