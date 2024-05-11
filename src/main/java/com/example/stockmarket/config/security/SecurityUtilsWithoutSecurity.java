package com.example.stockmarket.config.security;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@ConditionalOnProperty(name = "security.enabled", havingValue = "false")
@Component
@RequiredArgsConstructor
public class SecurityUtilsWithoutSecurity implements SecurityUtils {
    @Override
    public Participant getCurrentParticipant() {
        Participant admin = new Participant();
        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(adminRole);
        admin.setRoles(roleSet);
        return admin;
    }
}
