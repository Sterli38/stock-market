package com.example.stockmarket.service.participantService;

import com.example.stockmarket.dao.repositories.ParticipantRepository;
import com.example.stockmarket.dao.repositories.RoleRepository;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final RoleRepository roleRepository;

    public Participant createParticipant(Participant participant) {
        Set<Role> participantRoles = participant.getRoles().stream()
                .map(role -> roleRepository.getByRoleName(role.getRoleName()))
                .collect(Collectors.toSet());

        participant.setRoles(participantRoles);
        return participantRepository.save(participant);
    }

    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id).orElse(null);
    }

    public Participant getParticipantByName(String name) {
        return participantRepository.findByName(name);
    }

    public Participant editParticipant(Participant participant) {
        Set<Role> participantRoles = participant.getRoles().stream()
                .map(role -> roleRepository.getByRoleName(role.getRoleName()))
                .collect(Collectors.toSet());

        participant.setRoles(participantRoles);

        return participantRepository.save(participant);
    }

    public Participant deleteParticipantById(long id) {
        Participant returnParticipant = participantRepository.findById(id).orElse(null);
        participantRepository.deleteById(id);
        return returnParticipant;
    }
}
