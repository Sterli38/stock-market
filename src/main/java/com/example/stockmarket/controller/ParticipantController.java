package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.participantRequest.CreateParticipantRequest;
import com.example.stockmarket.controller.request.participantRequest.DeleteParticipantRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/participant")
public class ParticipantController {
    private final ParticipantService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/USER/create")
    public ParticipantResponse createParticipant(@RequestBody CreateParticipantRequest createParticipantRequest) {
        Participant participant = convertParticipantRequest(createParticipantRequest);
        return convertParticipant(service.createParticipant(participant));
    }

    @GetMapping("/ADMIN/getById")
    public ResponseEntity<ParticipantResponse> getParticipantById(@RequestBody GetParticipantByIdRequest getParticipantByIdRequest) {
        Participant participant = service.getParticipantById(getParticipantByIdRequest.getId());
        if (participant != null) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/ADMIN/getByName")
    public ResponseEntity<ParticipantResponse> getParticipantByName(@RequestBody GetParticipantByNameRequest getParticipantByNameRequest) {
        Participant participant = service.getParticipantByName(getParticipantByNameRequest.getName());
        if(participant != null) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/USER/edit")
    public ParticipantResponse editParticipant(@RequestBody UpdateParticipantRequest updateParticipantRequest) {
        Participant participant = service.editParticipant(convertParticipantRequest(updateParticipantRequest));
        return convertParticipant(participant);
    }

    @DeleteMapping("/ADMIN/delete")
    public ParticipantResponse deleteParticipantById(@RequestBody DeleteParticipantRequest deleteParticipantRequest) {
        return convertParticipant(service.deleteParticipantById(deleteParticipantRequest.getId()));
    }

    private Participant convertParticipantRequest(CreateParticipantRequest participantRequest) {
        Participant participant = new Participant();
        participant.setId(participantRequest.getId());
        participant.setName(participantRequest.getName());
        participant.setRoles(participantRequest.getRole());
        participant.setCreationDate(participantRequest.getCreationDate());
        participant.setPassword(passwordEncoder.encode(participantRequest.getPassword()));
        return participant;
    }

    private ParticipantResponse convertParticipant(Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(participant.getId());
        participantResponse.setName(participant.getName());
        participantResponse.setRole(participant.getRoles());
        participantResponse.setCreationDate(participant.getCreationDate().getTime());
        return participantResponse;
    }
}
