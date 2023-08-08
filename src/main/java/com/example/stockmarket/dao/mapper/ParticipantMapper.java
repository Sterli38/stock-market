package com.example.stockmarket.dao.mapper;

import com.example.stockmarket.entity.Participant;
import org.springframework.jdbc.core.RowMapper;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipantMapper implements RowMapper<Participant> {
    @Override
    public Participant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Participant participant = new Participant();

        participant.setId(rs.getLong("id"));
        participant.setName(rs.getString("name"));
        participant.setCreationDate(rs.getTimestamp("creation_date"));
        participant.setPassword(rs.getString("password"));
        return participant;
    }
}
