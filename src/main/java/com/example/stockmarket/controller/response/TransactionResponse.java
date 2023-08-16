package com.example.stockmarket.controller.response;

import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse {
    private Long TransactionId;
    private OperationType operationType;
    private Date date;
    private String receivedCurrency;
    private Double receivedAmount;
    private String givenCurrency;
    private Double givenAmount;
    private Participant participant;
    private double commission;
}
