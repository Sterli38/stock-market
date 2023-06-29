package com.example.stockmarket.controller.ParticipantController;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participant")
public class ParticipantController {
    private final ParticipantService service;

    @PostMapping("/create")
    public ParticipantResponse createParticipant(@RequestBody ParticipantRequest participantRequest) {
        Participant participant = convertParticipantRequest(participantRequest);
        return convertParticipant(service.createParticipant(participant));
    }

    @GetMapping("/get/{id}")
    public ParticipantResponse getParticipantById(@PathVariable long id) {
        return convertParticipant(service.getParticipantById(id));
    }

//    @PostMapping("/edit")
//    public ParticipantResponse editParticipant(@RequestBody ParticipantRequest participantRequest) {
//        service.editParticipant(convertParticipantRequest(participantRequest));
//        return ;
//    }

    @DeleteMapping("/delete/{id}")
    public ParticipantResponse deleteParticipantById(@PathVariable long id) {
        return convertParticipant(service.deleteParticipantById(id));
    }

    private Participant convertParticipantRequest(ParticipantRequest participantRequest) {
        Participant participant = new Participant();
        participant.setName(participantRequest.getName());
        participant.setCreationDate(participantRequest.getCreationDate());
        participant.setPassword(participantRequest.getPassword());
        return participant;
    }

    private ParticipantResponse convertParticipant(Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(participant.getId());
        participantResponse.setName(participant.getName());
        participantResponse.setCreationDate(new java.sql.Date(participant.getCreationDate().getTime()));
        return participantResponse;
    }
}
