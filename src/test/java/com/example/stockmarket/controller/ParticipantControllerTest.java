package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.participantRequest.CreateParticipantRequest;
import com.example.stockmarket.controller.request.participantRequest.UpdateParticipantRequest;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.service.participantService.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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
        Set<Role> egorRoles = new HashSet<>();
        egorRoles.add(Role.USER);
        egor.setRoles(egorRoles);
        egor.setCreationDate(new Date(1687791478000L));
        egor.setPassword("testPasswordEgor");
        long egorId = service.createParticipant(egor).getId();
        lena = new Participant();
        lena.setName("Lena");
        Set<Role> lenaRoles = new HashSet<>();
        lenaRoles.add(Role.USER);
        lenaRoles.add(Role.READER);
        lena.setRoles(lenaRoles);
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
        CreateParticipantRequest testParticipant = new CreateParticipantRequest();
        testParticipant.setName("participant");
        Set<Role> testRoles = new HashSet<>();
        testRoles.add(Role.USER);
        testRoles.add(Role.READER);
        testParticipant.setRoles(testRoles);
        testParticipant.setPassword("TestPassword");
        testParticipant.setCreationDate(new Date(1687532277000L));
        testParticipant.setEnabled(true);

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray expectedRoles = (JSONArray) jsonParser.parse(mapper.writeValueAsString(testRoles));

        MvcResult result = mockMvc.perform(post("/participant/create")
                        .content(mapper.writeValueAsString(testParticipant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(testParticipant.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(testParticipant.getCreationDate()))
                .andExpect(jsonPath("$.enabled").value(testParticipant.isEnabled()))
                .andReturn();

        int id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        testParticipant.setId(Long.valueOf(id));

        mockMvc.perform(get("/participant/getById")
                        .content(mapper.writeValueAsString(testParticipant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(testParticipant.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(testParticipant.getCreationDate()))
                .andExpect(jsonPath("$.enabled").value(testParticipant.isEnabled()));
    }

    @Test
    void getParticipantById() throws Exception {

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray expectedRoles = (JSONArray) jsonParser.parse(mapper.writeValueAsString(egor.getRoles()));

        mockMvc.perform(get("/participant/getById")
                        .content(mapper.writeValueAsString(egor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(egor.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(egor.getCreationDate().getTime()))
                .andExpect(jsonPath("$.enabled").value(egor.isEnabled()));
    }


    @Test
    void editParticipant() throws Exception {
        UpdateParticipantRequest updateForParticipant = new UpdateParticipantRequest();
        updateForParticipant.setId(egor.getId());
        updateForParticipant.setName("testName");
        Set<Role> testRoles = new HashSet<>();
        testRoles.add(Role.ADMIN);
        updateForParticipant.setRoles(testRoles);
        updateForParticipant.setCreationDate(new Date(1688059945000L));
        updateForParticipant.setPassword("testPassword");

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray expectedRoles = (JSONArray) jsonParser.parse(mapper.writeValueAsString(testRoles));

        mockMvc.perform(post("/participant/edit")
                        .content(mapper.writeValueAsString(updateForParticipant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(updateForParticipant.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(updateForParticipant.getCreationDate()))
                .andExpect(jsonPath("$.enabled").value(updateForParticipant.isEnabled()))
                .andReturn();

        mockMvc.perform(get("/participant/getById")
                        .content(mapper.writeValueAsString(updateForParticipant))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(updateForParticipant.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(updateForParticipant.getCreationDate().getTime()))
                .andExpect(jsonPath("$.enabled").value(updateForParticipant.isEnabled()));
    }


    @Test
    void deactivationParticipantById() throws Exception {

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray expectedRoles = (JSONArray) jsonParser.parse(mapper.writeValueAsString(egor.getRoles()));

        egor.setEnabled(true);

        mockMvc.perform(delete("/participant/deactivation")
                        .content(mapper.writeValueAsString(egor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(egor.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(egor.getCreationDate()))
                .andExpect(jsonPath("$.enabled").value(false));

        mockMvc.perform(get("/participant/getById")
                        .content(mapper.writeValueAsString(egor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(egor.getName()))
                .andExpect(jsonPath("$.roles").value(expectedRoles))
                .andExpect(jsonPath("$.creationDate").value(egor.getCreationDate()))
                .andExpect(jsonPath("$.enabled").value(false));
    }
}