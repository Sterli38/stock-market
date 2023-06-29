package com.example.stockmarket.controller.ParticipantController;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class ParticipantRequest {
    @NotEmpty
    private String name;
    @NotNull
    private Date creationDate;
    @Size(min = 0, max = 30)
    @NotEmpty
    private String password;
}
