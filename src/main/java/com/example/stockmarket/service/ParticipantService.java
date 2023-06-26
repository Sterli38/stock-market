package com.example.stockmarket.service;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.entity.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Participant editParticipant(Participant participant) {
        return dao.editParticipant(participant);
    }

    public Participant deleteParticipantById(Long id) {
        return dao.deleteParticipantById(id);
    }
}
