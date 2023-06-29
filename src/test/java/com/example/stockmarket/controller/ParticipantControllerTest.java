package com.example.stockmarket.controller;

import com.example.stockmarket.controller.ParticipantController.ParticipantRequest;
import com.example.stockmarket.controller.ParticipantController.ParticipantResponse;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.ParticipantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ParticipantControllerTest {
    @Autowired
    private ParticipantService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private Participant egor = new Participant();
    private Participant lena = new Participant();

    @BeforeEach
    void setup() {
        egor.setName("Egor");
        egor.setCreationDate(new Date(1687791478000L));
        egor.setPassword("testPasswordEgor");
        long egorId = service.createParticipant(egor).getId();
        lena.setName("Lena");
        lena.setCreationDate(new Date(1687532277000L));
        lena.setPassword("testPasswordLena");
        long lenaId = service.createParticipant(lena).getId();
        egor.setId(egorId);
        lena.setId(lenaId);
    }

    @AfterEach
    void after() {
        service.deleteParticipantById(egor.getId());
        service.deleteParticipantById(lena.getId());
    }

    @Test
    void createParticipant() throws Exception {
        Participant testParticipant = new Participant();
        Date date = new Date(1687532277);
        testParticipant.setName("TestName");
        testParticipant.setCreationDate(date);
        testParticipant.setPassword("TestPassword");
        ParticipantResponse expectedParticipant = convertParticipant(testParticipant);


        mockMvc.perform(post("/participant/create")
                .content(mapper.writeValueAsString(testParticipant))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(expectedParticipant.getName()));
//                .andExpect(jsonPath("$.creationDate").value(expectedParticipant.getCreationDate()));
    }

    @Test
    void getParticipantById() throws Exception {
        ParticipantResponse testParticipant = convertParticipant(egor);
        mockMvc.perform(get("/participant/get/{id}", egor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testParticipant.getName()));
//                .andExpect(jsonPath("$.creationDate").value(testParticipant.getCreationDate()));
    }

//    @Test
//    void editParticipant() throws Exception {
//        Participant egorClone = new Participant();
//        egorClone.setId(egor.getId());
//        egorClone.setName(egor.getName());
//        egorClone.setPassword(egor.getPassword());
//        egorClone.setCreationDate(egor.getCreationDate());
//        Participant updateForParticipant = new Participant();
//        updateForParticipant.setId(egorClone.getId());
//        updateForParticipant.setName("Mike");
//        updateForParticipant.setCreationDate(new Date(1688059945000L));
//        updateForParticipant.setPassword("testPassword");
//
//        ParticipantResponse expectedResponse = convertParticipant(updateForParticipant);
//
//        mockMvc.perform(post("/participant/edit")
//                .content(mapper.writeValueAsString(updateForParticipant))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.name").value(expectedResponse.getName()));
////                .andExpect(jsonPath("$.creationDate").value(expectedResponse.getCreationDate()));
//    }

    @Test
    void deleteParticipantById() throws Exception {
        ParticipantResponse testParticipant = convertParticipant(egor);
        mockMvc.perform(delete("/participant/delete/{id}", egor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testParticipant.getName()));
//               .andExpect(jsonPath("$.creationDate").value(testParticipant.getCreationDate()));
    }

    private ParticipantResponse convertParticipant(Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(participant.getId());
        participantResponse.setName(participant.getName());
        participantResponse.setCreationDate(new java.sql.Date(participant.getCreationDate().getTime()));
        return participantResponse;
    }
}