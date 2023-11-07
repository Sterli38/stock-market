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

        Role roleUser = new Role();
        roleUser.setRoleName("USER");
        Role roleReader = new Role();
        roleReader.setRoleName("READER");

        egor = new Participant();
        egor.setName("Egor");
        Set<Role> egorRoles = new HashSet<>();
        egorRoles.add(roleUser);
        egor.setRoles(egorRoles);
        egor.setPassword("testPasswordForEgor");
        egor.setEnabled(true);
        egor.setCreationDate(new Date(1687791478000L));
        Participant egorr = dao.createParticipant(egor);
        egor.setId(egorr.getId());
        egor.setRoles(egorr.getRoles());

        lena = new Participant();
        lena.setName("Lena");
        Set<Role> lenaRoles = new HashSet<>();
        lenaRoles.add(roleUser);
        lenaRoles.add(roleReader);
        lena.setRoles(lenaRoles);
        lena.setPassword("testPasswordForLena");
        lena.setEnabled(false);
        lena.setCreationDate(new Date(1687532277000L));
        Participant lenaa = dao.createParticipant(lena);
        lena.setId(lenaa.getId());
        lena.setRoles(lenaa.getRoles());
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
        Set<Role> expectedParticipantRoles = new HashSet<>();
        expectedParticipantRoles.addAll(lena.getRoles());
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
        Role roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setRoleName("USER");
        expectedParticipantRoles.add(roleUser);
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
}