package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Participant;

public interface ParticipantDao {
    Participant createParticipant(Participant participant);

    Participant getParticipantById(Long id);

    Participant editParticipant(Participant participant);

    Participant deleteParticipantById(Long id);
}
