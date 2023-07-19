package com.example.stockmarket.controller.request;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class ParticipantRequest {
    @NotNull
    @Positive
    private Long id;
}
