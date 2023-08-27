package com.example.stockmarket.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
public class User {
    private String username;
    private String password;
    private boolean enabled;
    private Set<GrantedAuthority> authorities;
}
