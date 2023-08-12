package com.example.stockmarket.dao.database;

import com.example.stockmarket.entity.Participant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

@JdbcTest
public class ParticipantDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Participant egor;
    private Participant lena;
    private ParticipantDatabaseDao dao;

    @BeforeEach
    void setup() {
        dao = new ParticipantDatabaseDao(jdbcTemplate);

        egor = new Participant();
        egor.setName("Egor");
        egor.setCreationDate(new Date(1687791478000L));
        egor.setPassword("testPasswordForEgor");
        Long egorId = dao.createParticipant(egor).getId();
        egor.setId(egorId);

        lena = new Participant();
        lena.setName("Lena");
        lena.setCreationDate(new Date(1687532277000L));
        lena.setPassword("testPasswordForLena");
        Long idLena = dao.createParticipant(lena).getId();
        lena.setId(idLena);
    }

    @AfterEach
    void after() {
        dao.deleteParticipantById(egor.getId());
        dao.deleteParticipantById(lena.getId());
    }

    @Test
    void createParticipant() {
        Participant expectedParticipant = new Participant();
        expectedParticipant.setName("Test");
        expectedParticipant.setCreationDate(new Date(1383532237000L));
        expectedParticipant.setPassword("P");

        Participant newParticipant = new Participant();
        newParticipant.setName("Test");
        newParticipant.setCreationDate(new Date(1383532237000L));
        newParticipant.setPassword("P");

        Participant responseParticipant = dao.createParticipant(newParticipant);

        expectedParticipant.setId(responseParticipant.getId());

        Assertions.assertEquals(expectedParticipant, responseParticipant);

    }

    @Test
    void getParticipantById() {
        Participant expectedParticipant = new Participant();
        expectedParticipant.setId(lena.getId());
        expectedParticipant.setName(lena.getName());
        expectedParticipant.setCreationDate(lena.getCreationDate());
        expectedParticipant.setPassword(lena.getPassword());

        Assertions.assertEquals(expectedParticipant, dao.getParticipantById(lena.getId()));

//        System.out.println(expectedParticipant.getCreationDate());
//        System.out.println(dao.getParticipantById(lena.getId()).getCreationDate());
//        Fri Jun 23 17:57:57 MSK 2023
//        2023-06-23 17:57:57.0
    }

    @Test
    void editParticipant() {
        Participant expected = new Participant();
        expected.setId(egor.getId());
        expected.setName("newParticipant");
        expected.setCreationDate(new Date(1383532237000L));
        expected.setPassword("testPassword");

        Participant updateParticipant = new Participant();
        updateParticipant.setId(egor.getId());
        updateParticipant.setName("newParticipant");
        updateParticipant.setCreationDate(new Date(1383532237000L));
        updateParticipant.setPassword("testPassword");

        Participant actualParticipant = dao.editParticipant(updateParticipant);

        Assertions.assertEquals(expected, actualParticipant);
    }

    @Test
    void deleteParticipantById() {
        dao.deleteParticipantById(1L);
        Assertions.assertNull(dao.getParticipantById(1));
    }
}