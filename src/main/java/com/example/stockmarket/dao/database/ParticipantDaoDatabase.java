package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.dao.ParticipantMapper;
import com.example.stockmarket.entity.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class ParticipantDaoDatabase implements ParticipantDao {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public Participant createParticipant(Participant participant) {
        String sql = "INSERT INTO participant(name, creation_date, password) values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, participant.getName());
            ps.setTimestamp(2,  new Timestamp(participant.getCreationDate().getTime()));
            ps.setString(3, participant.getPassword());
            return ps;
        }, keyHolder);
        return getParticipantById(keyHolder.getKey().longValue());
    }

    @Override
    public Participant getParticipantById(Long id) {
        String sql = "SELECT * FROM participant WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), id);
    }

    @Override
    public void editParticipant(Long id, Participant participant) {
        String sql = "UPDATE participant SET name = ?, creation_date = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, participant.getName(), participant.getCreationDate(), participant.getPassword(), participant.getId());
    }

    @Override
    public void deleteParticipantById(Long id) {
        String sql = "DELETE * FROM participant WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void clear() {
        String sql = "DELETE * FROM participant";
        jdbcTemplate.update(sql);
    }
}
