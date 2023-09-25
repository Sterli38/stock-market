package com.example.stockmarket.service.participantService;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.entity.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantDao dao;

    public Participant createParticipant(Participant participant) {
        return dao.createParticipant(participant);
    }

    public Participant getParticipantById(Long id) {
        return dao.getParticipantById(id);
    }

    public Participant getParticipantByName(String name) {
        return dao.getParticipantByName(name);
    }

    public Participant editParticipant(Participant participant) {
        return dao.editParticipant(participant);
    }

    public Participant deactivationParticipantById(Long id) {
        return dao.deactivationParticipantById(id);
    }

    public Participant deleteParticipantById(long id) {
        return dao.deleteParticipantById(id);
    }
}
