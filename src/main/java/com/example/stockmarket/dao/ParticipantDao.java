package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Participant;

public interface ParticipantDao {
    Participant createParticipant(Participant participant);
    Participant getParticipantById(Long id);
    void editParticipant(Long id, Participant participant);
    void deleteParticipantById(Long id);
    void clear();
}
