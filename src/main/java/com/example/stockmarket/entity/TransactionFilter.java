package com.example.stockmarket.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransactionFilter {
    private Long participantId;
    private OperationType operationType;
    private Date after;
    private Date before;
    private Double receivedMinAmount;
    private Double receivedMaxAmount;
    private Double givenMaxAmount;
    private Double givenMinAmount;
    private List<String> givenCurrencies;
    private List<String> receivedCurrencies;
}
