package com.example.stockmarket.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateParticipantRequest extends ParticipantRequest {
    @NotEmpty
    private String name;
    @NotNull
    private Date creationDate;
    @Size(min = 0, max = 30)
    @NotEmpty
    private String password;
}
