package com.example.stockmarket.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Transaction {
    private Long id;
    private OperationType operationType;
    private Date date;
    private String receivedCurrency;
    private Double receivedAmount;
    private String givenCurrency;
    private Double givenAmount;
    private Participant participant;
    private Double commission;
}
