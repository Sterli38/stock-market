package com.example.stockmarket.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Date;

public class GetParticipantIdRequest extends ParticipantRequest {
    @NotNull
    private Long id;
    @Null
    private String name;
    @Null
    private Date creationDate;
    @Null
    private String password;
}
