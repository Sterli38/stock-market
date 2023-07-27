package com.example.stockmarket.controller.request.participantRequest;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class ParticipantRequest {
    @NotNull
    @Positive
    private Long id;
}
