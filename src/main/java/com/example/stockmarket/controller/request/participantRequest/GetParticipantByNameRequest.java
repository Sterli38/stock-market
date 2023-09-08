package com.example.stockmarket.controller.request.participantRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetParticipantByNameRequest {
    @NotNull
    private String name;
}
