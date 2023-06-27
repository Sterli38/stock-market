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
        String sql = "INSERT INTO participant(name, creation_date, password) values(?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), participant.getName(), participant.getCreationDate(), participant.getPassword());
    }

    @Override
    @Nullable
    public Participant getParticipantById(long id) {
        String sql = "SELECT * FROM participant WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Participant editParticipant(Participant participant) {
//        String sqlQueryForUpdate = "UPDATE participant SET name = ?, creation_date = ?, password = ? WHERE id = ?";
//        String sqlQueryForSelect = "SELECT * FROM participant WHERE id = ?";
//        jdbcTemplate.update(sqlQueryForUpdate, participant.getName(), participant.getCreationDate(), participant.getPassword(), participant.getId());
//        return jdbcTemplate.queryForObject(sqlQueryForSelect, new ParticipantMapper(), participant.getId());

        String sql = "UPDATE participant SET name = ?, creation_date = ?, password = ? WHERE id = ? RETURNING *";
//        String sql = "UPDATE participant SET name = 'Mike', creation_date = '2023-06-26' , password = 'password' WHERE id = 862769  RETURNING *";

        return jdbcTemplate.queryForObject(sql, new ParticipantMapper(),participant.getName(), participant.getCreationDate(), participant.getPassword(), participant.getId());
    }

    @Override
    public Participant deleteParticipantById(long id) {
//        Participant returnParticipant = getParticipantById(id);
//        String sql = "DELETE * FROM participant WHERE id = ?";
//        jdbcTemplate.update(sql, id);
//        return returnParticipant;
        String sql = "DELETE FROM participant WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), id);
    }
}