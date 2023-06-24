package com.example.stockmarket.service;

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

    public void editParticipant(Long id, Participant participant) {
        dao.editParticipant(id, participant);
    }

    public void deleteParticipantById(Long id) {
        dao.deleteParticipantById(id);
    }
}
