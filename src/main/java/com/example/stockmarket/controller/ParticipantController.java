package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.ParticipantRequest;
import com.example.stockmarket.controller.response.ParticipantResponse;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.participantService.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
    public ResponseEntity<ParticipantResponse> getParticipantById(@PathVariable long id) {
        Participant participant = service.getParticipantById(id);
        if ( participant != null ) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/edit")
    public ParticipantResponse editParticipant(@RequestBody ParticipantRequest participantRequest) {
        Participant participant = service.editParticipant(convertParticipantRequest(participantRequest));
        ParticipantResponse participantResponse = convertParticipant(participant);
        return participantResponse;
    }

    @DeleteMapping("/delete/{id}")
    public ParticipantResponse deleteParticipantById(@PathVariable long id) {
        return convertParticipant(service.deleteParticipantById(id));
    }

    private Participant convertParticipantRequest(ParticipantRequest participantRequest) {
        Participant participant = new Participant();
        participant.setId(participantRequest.getId());
        participant.setName(participantRequest.getName());
        participant.setCreationDate(participantRequest.getCreationDate());
        participant.setPassword(participantRequest.getPassword());
        return participant;
    }

    private ParticipantResponse convertParticipant(Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(participant.getId());
        participantResponse.setName(participant.getName());
        participantResponse.setCreationDate(participant.getCreationDate().getTime());
        return participantResponse;
    }
}
