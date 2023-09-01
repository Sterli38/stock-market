package com.example.stockmarket.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Set;

@Data
public class Participant {
    private Long id;
    private String name;
    @ToString.Exclude
    private String password;
    private Set<Role> roles;
//    private Set<GrantedAuthority> roles;
    private boolean enabled;
    private Date creationDate;
}
