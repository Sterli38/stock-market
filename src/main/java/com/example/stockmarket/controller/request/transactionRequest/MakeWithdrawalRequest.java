package com.example.stockmarket.controller.request.transactionRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MakeWithdrawalRequest extends AbstractRequest {
    @NotNull
    private String givenCurrency;
    @NotNull
    private Double givenAmount;
}
