package com.example.stockmarket.controller;

import com.example.stockmarket.controller.ParticipantController.request.ParticipantRequest;
import com.example.stockmarket.controller.ParticipantController.response.ParticipantResponse;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.participantService.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;


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

    private Participant egor;
    private Participant lena;

    @BeforeEach
    void setup() {
        egor = new Participant();
        egor.setName("Egor");
        egor.setCreationDate(new Date(1687791478000L));
        egor.setPassword("testPasswordEgor");
        long egorId = service.createParticipant(egor).getId();
        lena = new Participant();
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
        ParticipantRequest testParticipant = new ParticipantRequest();
        Date date = new Date(1687532277000L);
        testParticipant.setName("TestName");
        testParticipant.setCreationDate(date);
        testParticipant.setPassword("TestPassword");

        MvcResult result = mockMvc.perform(post("/participant/create")
                        .content(mapper.writeValueAsString(testParticipant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(testParticipant.getName()))
                .andExpect(jsonPath("$.creationDate").value(testParticipant.getCreationDate()))
                .andReturn();

        int id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/participant/get/{id}", id))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(testParticipant.getName()))
                .andExpect(jsonPath("$.creationDate").value(testParticipant.getCreationDate()));
    }

    @Test
    void getParticipantById() throws Exception {
        mockMvc.perform(get("/participant/get/{id}", egor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(egor.getName()))
                .andExpect(jsonPath("$.creationDate").value(egor.getCreationDate().getTime()));
    }


    @Test
    void editParticipant() throws Exception {
        ParticipantRequest updateForParticipant = new ParticipantRequest();
        updateForParticipant.setId(egor.getId());
        updateForParticipant.setName("testName");
        updateForParticipant.setCreationDate(new Date(1688059945000L));
        updateForParticipant.setPassword("testPassword");

        mockMvc.perform(post("/participant/edit")
                        .content(mapper.writeValueAsString(updateForParticipant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(updateForParticipant.getName()))
                .andExpect(jsonPath("$.creationDate").value(updateForParticipant.getCreationDate()))
                .andReturn();

        mockMvc.perform(get("/participant/get/{id}", updateForParticipant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(updateForParticipant.getName()))
                .andExpect(jsonPath("$.creationDate").value(updateForParticipant.getCreationDate().getTime()));
    }


    @Test
    void deleteParticipantById() throws Exception {
        mockMvc.perform(delete("/participant/delete/{id}", egor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(egor.getName()))
                .andExpect(jsonPath("$.creationDate").value(egor.getCreationDate()));

        mockMvc.perform(get("/participant/get/{id}", egor.getId()))
                .andExpect(status().isNotFound());
    }
}