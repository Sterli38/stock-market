package com.example.stockmarket.controller.request.participantRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class ParticipantRequest {
    @NotNull
    @Positive
    private Long id;
}
