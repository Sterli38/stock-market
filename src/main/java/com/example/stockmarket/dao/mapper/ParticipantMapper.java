package com.example.stockmarket.dao.mapper;

import com.example.stockmarket.entity.Participant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipantMapper implements RowMapper<Participant> {
    @Override
    public Participant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Participant participant = new Participant();

        participant.setId(rs.getLong("participant_id"));
        participant.setName(rs.getString("participant_name"));
        participant.setPassword(rs.getString("password"));
        participant.setCreationDate(rs.getTimestamp("creation_date"));
        participant.setEnabled(rs.getBoolean("enabled"));
        return participant;
    }
}
