package com.example.stockmarket.service.participantService;

import com.example.stockmarket.dao.repositories.ParticipantRepository;
import com.example.stockmarket.entity.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public Participant createParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id).orElse(null);
    }

    public Participant getParticipantByName(String name) {
        return participantRepository.findByName(name);
    }

    public Participant editParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    public Participant deleteParticipantById(long id) {
        Participant returnParticipant = participantRepository.findById(id).orElse(null);
        participantRepository.deleteById(id);
        return returnParticipant;
    }
}
