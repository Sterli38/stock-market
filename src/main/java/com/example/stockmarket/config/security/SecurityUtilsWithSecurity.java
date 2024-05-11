package com.example.stockmarket.config.security;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.participantService.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "security.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class SecurityUtilsWithSecurity implements SecurityUtils {
    private final ParticipantService participantService;
    public Participant getCurrentParticipant() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return participantService.getParticipantByName(username);
    }
}
