package com.example.stockmarket.exception;

public class NoAccessForEditParticipantException extends UserException {

    public NoAccessForEditParticipantException() {
        super("Insufficient rights to edit another participant");
    }
}
