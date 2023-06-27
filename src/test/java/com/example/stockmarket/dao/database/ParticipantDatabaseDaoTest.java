package com.example.stockmarket.dao.database;

import org.springframework.jdbc.core.JdbcTemplate;

public class ParticipantDatabaseDaoTest extends ParticipantDaoTest<ParticipantDatabaseDao> {

    public ParticipantDatabaseDaoTest() {
        dao = new ParticipantDatabaseDao(new JdbcTemplate());
    }
}
