package com.example.stockmarket.exception;

public class ParticipantNotFoundException extends UserException {
    public ParticipantNotFoundException(Long participantId) {
        super("Participant with id " + participantId +" not found");
    }
}
