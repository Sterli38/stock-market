package com.example.stockmarket.controller.ParticipantController.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class ParticipantRequest {
    @NotNull
    @Positive
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    private Date creationDate;
    @Size(min = 0, max = 30)
    @NotEmpty
    private String password;
}
