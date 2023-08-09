package com.example.stockmarket.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Transaction {
    private Long id;
    private OperationType operationType;
    private Date date;
    private Double receivedAmount;
    private Double givenAmount;
    private Participant participant;
    private String receivedCurrency;
    private String givenCurrency;
    private double commission;
}
