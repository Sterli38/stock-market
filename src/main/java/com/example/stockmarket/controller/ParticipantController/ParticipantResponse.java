package com.example.stockmarket.controller.ParticipantController;

import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Date;

@Data
public class ParticipantResponse {
    private Long id;
    private String name;
    private Date creationDate;
}
