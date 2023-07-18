package com.example.stockmarket.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Transaction {
    private Long id;
    private String operationType;
    private Date date;
    private double amount;
    private Long participantId;
    private String receivedCurrency;
    private String givenCurrency;
    private double commission;
}
