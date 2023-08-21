package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetTransactionsRequest {
    @NotNull
    private Long participantId;
    private String operationType;
    private Date after;
    private Date before;
    private Double receivedMinAmount;
    private Double receivedMaxAmount;
    private Double givenMinAmount;
    private Double givenMaxAmount;
    private List<String> givenCurrencies;
    private List<String> receivedCurrencies;
}
