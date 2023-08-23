package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipantMapper implements RowMapper<Participant> {
    @Override
    public Participant mapRow(ResultSet rs, int rowNum) throws SQLException {
        RoleMapper roleMapper = new RoleMapper();
        Role role = roleMapper.mapRow(rs, rowNum);

        Participant participant = new Participant();

        participant.setId(rs.getLong("participant_id"));
        participant.setName(rs.getString("participant_name"));
        participant.setRole(role);
        participant.setCreationDate(rs.getTimestamp("creation_date"));
        participant.setPassword(rs.getString("password"));
        return participant;
    }
}
