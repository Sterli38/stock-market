package com.example.stockmarket.controller.request.transactionRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class AbstractRequest {
    @Positive
    @Schema(description = "Идентификатор участника")
    private Long participantId;
}
