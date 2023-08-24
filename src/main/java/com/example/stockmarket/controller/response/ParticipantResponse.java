package com.example.stockmarket.controller.response;

import com.example.stockmarket.entity.Role;
import lombok.Data;

@Data
public class ParticipantResponse {
    private Long id;
    private String name;
    private Role role;
    private Long creationDate;
}
