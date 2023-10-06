package com.example.stockmarket.controller.request.participantRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class ParticipantRequest {
    @Schema(description = "Идентификатор участника")
    @NotNull
    @Positive
    private Long id;
}
