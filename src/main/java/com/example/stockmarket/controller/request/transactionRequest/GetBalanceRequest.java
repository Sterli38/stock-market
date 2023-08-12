package com.example.stockmarket.controller.request.transactionRequest;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetBalanceRequest extends AbstractRequest {
    @NotNull
    private String currency;
}
