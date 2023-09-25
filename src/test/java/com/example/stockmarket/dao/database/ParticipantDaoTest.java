package com.example.stockmarket.dao.database;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
        Set<Role> egorRoles = new HashSet<>();
        egorRoles.add(Role.USER);
        egor.setRoles(egorRoles);
        egor.setPassword("testPasswordForEgor");
        egor.setEnabled(true);
        egor.setCreationDate(new Date(1687791478000L));
        Long egorId = dao.createParticipant(egor).getId();
        egor.setId(egorId);

        lena = new Participant();
        lena.setName("Lena");
        Set<Role> lenaRoles = new HashSet<>();
        lenaRoles.add(Role.USER);
        lenaRoles.add(Role.READER);
        lena.setRoles(lenaRoles);
        lena.setPassword("testPasswordForLena");
        lena.setEnabled(false);
        lena.setCreationDate(new Date(1687532277000L));
        Long idLena = dao.createParticipant(lena).getId();
        lena.setId(idLena);
    }

    @AfterEach
    void after() {
        dao.deactivationParticipantById(egor.getId());
        dao.deactivationParticipantById(lena.getId());
    }

    @Test
    void createParticipant() {
        Participant expectedParticipant = new Participant();
        expectedParticipant.setName("Test");
        Set<Role> expectedParticipantRoles = new HashSet<>();
        expectedParticipantRoles.add(Role.USER);
        expectedParticipantRoles.add(Role.READER);
        expectedParticipant.setRoles(expectedParticipantRoles);
        expectedParticipant.setPassword("P");
        expectedParticipant.setCreationDate(new Date(1383532237000L));
        expectedParticipant.setEnabled(true);

        Participant newParticipant = new Participant();
        newParticipant.setName("Test");
        newParticipant.setRoles(expectedParticipantRoles);
        newParticipant.setPassword("P");
        newParticipant.setCreationDate(new Date(1383532237000L));
        newParticipant.setEnabled(true);

        Participant responseParticipant = dao.createParticipant(newParticipant);

        expectedParticipant.setId(responseParticipant.getId());

        Assertions.assertEquals(expectedParticipant, responseParticipant);
    }

    @Test
    void getParticipantById() {
        Participant expectedParticipant = new Participant();
        expectedParticipant.setId(lena.getId());
        expectedParticipant.setName(lena.getName());
        expectedParticipant.setRoles(lena.getRoles());
        expectedParticipant.setPassword(lena.getPassword());
        expectedParticipant.setCreationDate(lena.getCreationDate());
        expectedParticipant.setEnabled(lena.isEnabled());

        Assertions.assertEquals(expectedParticipant, dao.getParticipantById(lena.getId()));
    }

    @Test
    void editParticipant() {
        Participant expected = new Participant();
        expected.setId(egor.getId());
        expected.setName("newParticipant");
        Set<Role> expectedParticipantRoles = new HashSet<>();
        expectedParticipantRoles.add(Role.USER);
        expected.setRoles(expectedParticipantRoles);
        expected.setPassword("testPassword");
        expected.setCreationDate(new Date(1383532237000L));
        expected.setEnabled(false);

        Participant updateParticipant = new Participant();
        updateParticipant.setId(egor.getId());
        updateParticipant.setName("newParticipant");
        updateParticipant.setRoles(expectedParticipantRoles);
        updateParticipant.setPassword("testPassword");
        updateParticipant.setCreationDate(new Date(1383532237000L));
        updateParticipant.setEnabled(false);

        Participant actualParticipant = dao.editParticipant(updateParticipant);

        Assertions.assertEquals(expected, actualParticipant);
    }

    @Test
    void deleteParticipantById() {
        Participant testParticipant = new Participant();
        testParticipant.setId(lena.getId());
        testParticipant.setName(lena.getName());
        testParticipant.setPassword(lena.getPassword());
        testParticipant.setCreationDate(lena.getCreationDate());
        testParticipant.setRoles(lena.getRoles());
        testParticipant.setEnabled(lena.isEnabled());
        lena.setEnabled(true);

        dao.deactivationParticipantById(testParticipant.getId());

        Assertions.assertEquals(testParticipant, dao.getParticipantById(testParticipant.getId()));
    }
}