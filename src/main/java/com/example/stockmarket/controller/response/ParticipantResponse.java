package com.example.stockmarket.controller.response;

import lombok.Data;

@Data
public class ParticipantResponse {
    private Long id;
    private String name;
    private Long creationDate;
}
