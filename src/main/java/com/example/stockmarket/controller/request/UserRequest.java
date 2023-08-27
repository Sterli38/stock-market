package com.example.stockmarket.controller.request;

import lombok.Data;
import java.util.Set;

@Data
public class UserRequest {
    private String username;
    private String password;
    private boolean enabled;
    private Set<String> authorities;
}
