package com.example.stockmarket.controller;

import com.example.stockmarket.config.security.SecurityUtils;
import com.example.stockmarket.controller.request.participantRequest.CreateParticipantRequest;
import com.example.stockmarket.controller.request.participantRequest.GetParticipantByIdRequest;
import com.example.stockmarket.controller.request.participantRequest.GetParticipantByNameRequest;
import com.example.stockmarket.controller.request.participantRequest.UpdateParticipantRequest;
import com.example.stockmarket.controller.response.ParticipantResponse;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.participantService.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ParticipantControllerImpl implements ParticipantController {
    private final ParticipantService service;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    public ParticipantResponse createParticipant(@RequestBody CreateParticipantRequest createParticipantRequest) {
        Participant participant = convertParticipantRequest(createParticipantRequest);
        return convertParticipant(service.createParticipant(participant));
    }

    public ResponseEntity<ParticipantResponse> getParticipantById(@RequestBody GetParticipantByIdRequest getParticipantByIdRequest) {
        Participant participant = service.getParticipantById(getParticipantByIdRequest.getId());
        if (participant != null) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ParticipantResponse> getParticipantByName(@RequestBody GetParticipantByNameRequest getParticipantByNameRequest) {
        Participant participant = service.getParticipantByName(getParticipantByNameRequest.getName());
        if (participant != null) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ParticipantResponse editParticipant(@RequestBody UpdateParticipantRequest updateParticipantRequest) {
//        Participant currentParticipant = securityUtils.getCurrentParticipant();
        Participant participant = service.editParticipant(convertParticipantRequest(updateParticipantRequest));
        return convertParticipant(participant);
    }

    private Participant convertParticipantRequest(CreateParticipantRequest participantRequest) {
        Participant participant = new Participant();
        participant.setId(participantRequest.getId());
        participant.setName(participantRequest.getName());
        participant.setPassword(passwordEncoder.encode(participantRequest.getPassword()));
        participant.setRoles(participantRequest.getRoles());
        participant.setCreationDate(participantRequest.getCreationDate());
        participant.setEnabled(participantRequest.isEnabled());
        return participant;
    }

    private ParticipantResponse convertParticipant(Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(participant.getId());
        participantResponse.setName(participant.getName());
        participantResponse.setRoles(participant.getRoles());
        participantResponse.setCreationDate(participant.getCreationDate().getTime());
        participantResponse.setEnabled(participant.isEnabled());
        return participantResponse;
    }
}
