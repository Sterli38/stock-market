package com.example.stockmarket.config.security;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;

import java.util.Objects;

public interface SecurityUtils {
    default boolean isOperationAvailableForCurrentParticipant(Long participantId) {
        Participant currentParticipant = getCurrentParticipant();
        boolean isAdmin = currentParticipant.getRoles().stream()
                .map(Role::getRoleName)
                .anyMatch(i -> i.equals("ADMIN"));
        return isAdmin || Objects.equals(currentParticipant.getId(), participantId);
    }

    Participant getCurrentParticipant();
}
