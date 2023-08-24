package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.dao.ParticipantMapper;
import com.example.stockmarket.entity.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class ParticipantDatabaseDao implements ParticipantDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Participant createParticipant(@Nullable Participant participant) {
        String sql = "INSERT INTO participant(name, role_id, creation_date, password) values(?, (SELECT id FROM role WHERE name = ?), ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, participant.getName());
            ps.setString(2, participant.getRole().name());
            ps.setTimestamp(3, new Timestamp(participant.getCreationDate().getTime()));
            ps.setString(4, participant.getPassword());
            return ps;
        }, keyHolder);
        Participant returnParticipant = getParticipantById(keyHolder.getKey().longValue());
        return returnParticipant;
    }

    @Override
    @Nullable
    public Participant getParticipantById(long id) {
        String sql = "SELECT participant.id as participant_id, participant.name as participant_name, role.name as role_name, creation_date, password FROM participant JOIN role on participant.role_id = role.id WHERE participant.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Participant editParticipant(Participant participant) {
        String sql = "UPDATE participant SET name = ?, role_id = (SELECT id FROM role WHERE name = ?), creation_date = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, participant.getName(), participant.getRole().name(), participant.getCreationDate(), participant.getPassword(), participant.getId());
        return getParticipantById(participant.getId());
    }

    @Override
    public Participant deleteParticipantById(long id) {
        Participant returnParticipant = getParticipantById(id);
        String sql = "DELETE FROM participant WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return returnParticipant;
    }
}
