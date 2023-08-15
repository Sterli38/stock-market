package com.example.stockmarket.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionFilter {
    private Long participantId;
    private OperationType operationType;
    private Date after;
    private Date before;
    private Double minAmount;
    private Double maxAmount;
    private String currency;
}
