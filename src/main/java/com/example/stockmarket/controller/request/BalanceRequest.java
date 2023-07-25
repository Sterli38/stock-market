package com.example.stockmarket.controller.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class BalanceRequest {
    @NotNull
    @Positive
    private Long participantId;
    @Nullable
    private String receivedCurrency;
}
