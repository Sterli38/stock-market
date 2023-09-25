package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Participant;

public interface ParticipantDao {
    Participant createParticipant(Participant participant);

    Participant getParticipantById(long id);

    Participant getParticipantByName(String name);

    Participant editParticipant(Participant participant);

    Participant deactivationParticipantById(long id);

    Participant deleteParticipantById(long id);
}
