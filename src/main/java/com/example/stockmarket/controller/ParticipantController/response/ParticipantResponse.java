package com.example.stockmarket.controller.ParticipantController.response;

import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Date;

@Data
public class ParticipantResponse {
    private Long id;
    private String name;
    private Long creationDate;
}
