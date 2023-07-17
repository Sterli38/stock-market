package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.CreateParticipantRequest;
import com.example.stockmarket.controller.request.GetParticipantIdRequest;
import com.example.stockmarket.controller.request.ParticipantRequest;
import com.example.stockmarket.controller.request.UpdateParticipantRequest;
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
    public ParticipantResponse createParticipant(@RequestBody CreateParticipantRequest createParticipantRequest) {
        Participant participant = convertParticipantRequest(createParticipantRequest);
        return convertParticipant(service.createParticipant(participant));
    }

    @GetMapping("/get")
    public ResponseEntity<ParticipantResponse> getParticipantById(@RequestBody GetParticipantIdRequest getParticipantIdRequest) {
        Participant participant = service.getParticipantById(getParticipantIdRequest.getId());
        if ( participant != null ) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/edit")
    public ParticipantResponse editParticipant(@RequestBody UpdateParticipantRequest updateParticipantRequest) {
        Participant participant = service.editParticipant(convertParticipantRequest(updateParticipantRequest));
        ParticipantResponse participantResponse = convertParticipant(participant);
        return participantResponse;
    }

    @DeleteMapping("/delete")
    public ParticipantResponse deleteParticipantById(@RequestBody GetParticipantIdRequest getParticipantIdRequest) {
        return convertParticipant(service.deleteParticipantById(getParticipantIdRequest.getId()));
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
