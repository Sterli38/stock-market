package com.example.stockmarket.config.security;

import com.example.stockmarket.controller.request.participantRequest.ParticipantRequest;
import com.example.stockmarket.dao.database.ParticipantDatabaseDao;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.service.participantService.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final ParticipantService participantService;

    public Participant getCurrentParticipant() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return participantService.getParticipantByName(username);
    }

    public boolean isOperationAvailableForCurrentParticipant(ParticipantRequest participantRequest) {
        Participant currentParticipant = getCurrentParticipant();
        if(currentParticipant.getRoles().contains(Role.ADMIN)) {
            return true;
        }
        return currentParticipant.getId() == participantRequest.getId();
    }
}
