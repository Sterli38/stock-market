package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.entity.Participant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;

public abstract class ParticipantDaoTest<T extends ParticipantDao> {

    protected T dao;
    private Participant egor;
    private Participant lena;

    @BeforeEach
    void setup() {
        egor = new Participant();
        egor.setName("Egor");
        egor.setCreationDate(new Date(1687791478));
        egor.setPassword("gfhjkm191");
        lena = new Participant();
        lena.setName("Lena");
        lena.setCreationDate(new Date(1687532277));
    }

    @AfterEach
    void after() {
        dao.deleteParticipantById(egor.getId());
        dao.deleteParticipantById(lena.getId());
    }

    @Test
    void createParticipant() {

    }

    @Test
    void getParticipantById() {
        Assertions.assertEquals(egor, dao.getParticipantById(egor.getId()));

    }

    @Test
    void editParticipant() {
    }

    @Test
    void deleteParticipantById() {

    }
}