package com.example.stockmarket.controller.response;

import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TransactionResponse {
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
