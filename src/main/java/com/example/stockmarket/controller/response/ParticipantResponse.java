package com.example.stockmarket.controller.response;

import com.example.stockmarket.entity.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
public class ParticipantResponse {
    private Long id;
    private String name;
    private Set<Role> role;
    private Long creationDate;
}
