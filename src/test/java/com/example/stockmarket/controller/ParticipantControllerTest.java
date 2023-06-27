package com.example.stockmarket.controller;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import java.sql.Date;

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
        egor = new Participant();
        egor.setName("Egor");
        egor.setCreationDate(new java.sql.Date(1687791478));
        egor.setPassword("gfhjkm191");
        lena = new Participant();
        lena.setName("Lena");
        lena.setCreationDate(new Date(1687532277));
    }

    @AfterEach
    void after() {
        service.deleteParticipantById(egor.getId());
        service.deleteParticipantById(lena.getId());
    }

    @Test
    void createParticipant() {

    }

    @Test
    void getParticipantById() {

    }

    @Test
    void editParticipant() {
    }

    @Test
    void deleteParticipantById() {

    }
}