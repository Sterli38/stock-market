package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ParticipantMapper implements RowMapper<Participant> {
    @Override
    public Participant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Participant participant = new Participant();

        participant.setId(rs.getLong("participant_id"));
        participant.setName(rs.getString("participant_name"));
        participant.setPassword(rs.getString("password"));
        participant.setEnabled(rs.getBoolean("enabled"));
        participant.setCreationDate(rs.getTimestamp("creation_date"));
        return participant;
    }
}
