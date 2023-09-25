package com.example.stockmarket.controller;

import com.example.stockmarket.config.security.SecurityUtils;
import com.example.stockmarket.controller.request.participantRequest.CreateParticipantRequest;
import com.example.stockmarket.controller.request.participantRequest.DeleteParticipantRequest;
import com.example.stockmarket.controller.request.participantRequest.GetParticipantByIdRequest;
import com.example.stockmarket.controller.request.participantRequest.GetParticipantByNameRequest;
import com.example.stockmarket.controller.request.participantRequest.UpdateParticipantRequest;
import com.example.stockmarket.controller.response.ErrorResponse;
import com.example.stockmarket.controller.response.ParticipantResponse;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.participantService.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Функционал участника", description = "Работа с пользователем")
public class ParticipantController {
    private final ParticipantService service;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Создание участника", description = "Позволяет участнику зарегистрироваться")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/create")
    public ParticipantResponse createParticipant(@RequestBody CreateParticipantRequest createParticipantRequest) {
        Participant participant = convertParticipantRequest(createParticipantRequest);
        return convertParticipant(service.createParticipant(participant));
    }

    @Operation(summary = "Поиск участника по id", description = "Позволяет найти участника по его id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    @GetMapping("/getById")
    public ResponseEntity<ParticipantResponse> getParticipantById(@RequestBody GetParticipantByIdRequest getParticipantByIdRequest) {
        Participant participant = service.getParticipantById(getParticipantByIdRequest.getId());
        if (participant != null) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Поиск участника по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    @GetMapping("/getByName")
    public ResponseEntity<ParticipantResponse> getParticipantByName(@RequestBody GetParticipantByNameRequest getParticipantByNameRequest) {
        Participant participant = service.getParticipantByName(getParticipantByNameRequest.getName());
        if (participant != null) {
            return ResponseEntity.ok(convertParticipant(participant));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Изменение данных участника", description = "Позволяет изменить данные существующего участника")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Participant not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/edit")
    public ParticipantResponse editParticipant(@RequestBody UpdateParticipantRequest updateParticipantRequest) {
//        Participant currrentParticipant = securityUtils.getCurrentParticipant();
        Participant participant = service.editParticipant(convertParticipantRequest(updateParticipantRequest));
        return convertParticipant(participant);
    }

    @Operation(summary = "Деактивация участника по id")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Participant not found")
            })
    @DeleteMapping("/delete")
    public ParticipantResponse deleteParticipantById(@RequestBody DeleteParticipantRequest deleteParticipantRequest) {
        return convertParticipant(service.deleteParticipantById(deleteParticipantRequest.getId()));
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
