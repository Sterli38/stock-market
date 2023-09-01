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
        participant.setRoles(Collections.singleton(Role.valueOf(rs.getString("role_name"))));
        participant.setCreationDate(rs.getTimestamp("creation_date"));
        participant.setPassword(rs.getString("password"));
        return participant;
    }
}
