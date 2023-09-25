package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.dao.mapper.ParticipantMapper;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ParticipantDatabaseDao implements ParticipantDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Participant createParticipant(@Nullable Participant participant) {
        String addParticipantSql = "INSERT INTO participant(name, password, enabled, creation_date) values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(addParticipantSql, new String[]{"id"});
            ps.setString(1, participant.getName());
            ps.setString(2, participant.getPassword());
            ps.setBoolean(3, participant.isEnabled());
            ps.setTimestamp(4, new Timestamp(participant.getCreationDate().getTime()));
            return ps;
        }, keyHolder);
        Long participantId = keyHolder.getKey().longValue();

        if(participant.getRoles() != null) {
            String roleSql = "INSERT INTO participant_to_role(participant_id, role_id) values(?, (SELECT role.id FROM role WHERE role.role_name = ?))";
            jdbcTemplate.batchUpdate(roleSql, participant.getRoles(), 100, (ps, role) -> {
                ps.setLong(1, participantId);
                ps.setString(2, role.name());
            });
        }

        Participant returnParticipant = getParticipantById(participantId);
        return returnParticipant;
    }

    @Override
    @Nullable
    public Participant getParticipantById(long id) {
        String participantSql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled FROM participant WHERE participant.id = ?";
        String roleSql = "SELECT role.role_name FROM role JOIN participant_to_role on role.id = participant_to_role.role_id WHERE participant_to_role.participant_id = ?";
        try {
            Participant participant = jdbcTemplate.queryForObject(participantSql, new ParticipantMapper(), id);
            List<Role> list = jdbcTemplate.queryForList(roleSql, Role.class, id);
            Set<Role> participantRoles = new HashSet<Role>(list);
            participant.setRoles(participantRoles);
            return participant;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Participant getParticipantByName(String name) {
        String participantSql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled FROM participant WHERE participant.name = ?";
        String roleSql = "SELECT role.role_name FROM role JOIN participant_to_role on role.id = participant_to_role.role_id WHERE participant_to_role.participant_id = ?";
        try {
            Participant participant = jdbcTemplate.queryForObject(participantSql, new ParticipantMapper(), name);
            List<Role> list = jdbcTemplate.queryForList(roleSql, Role.class, participant.getId());
            Set<Role> participantRoles = new HashSet<Role>(list);
            participant.setRoles(participantRoles);
            return participant;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Participant editParticipant(Participant participant) {
        String deleteRoles = "DELETE FROM participant_to_role WHERE participant_id = ?";
        jdbcTemplate.update(deleteRoles, participant.getId());
        if(participant.getRoles() != null) {
            String roleSql = "INSERT INTO participant_to_role(participant_id, role_id) values(?, (SELECT role.id FROM role WHERE role.role_name = ?))";
            jdbcTemplate.batchUpdate(roleSql, participant.getRoles(), 100, (ps, role) -> {
                ps.setLong(1, participant.getId());
                ps.setString(2, role.name());
            });
        }
        String participantSql = "UPDATE participant SET name = ?, password = ?, enabled = ?, creation_date = ? WHERE id = ?";

        jdbcTemplate.update(participantSql, participant.getName(), participant.getPassword(), participant.isEnabled(), participant.getCreationDate(), participant.getId());
        return getParticipantById(participant.getId());
    }

    @Override
    public Participant deactivationParticipantById(long id) {
//        Participant returnParticipant = getParticipantById(id);
        String sql = "UPDATE participant SET enabled = false WHERE id = ?";
        jdbcTemplate.update(sql, id);
        Participant returnParticipant = getParticipantById(id);
        return returnParticipant;
    }

    @Override
    public Participant deleteParticipantById(long id) {
        Participant returnParticipant = getParticipantById(id);
        String roleSql = "DELETE FROM participant_to_role WHERE participant_id = ?";
        jdbcTemplate.update(roleSql, id);
        String transactionSql = "DELETE FROM TRANSACTION WHERE participant_id = ?";
        jdbcTemplate.update(transactionSql, id);
        String participantsql = "DELETE FROM participant WHERE id = ?";
        jdbcTemplate.update(participantsql, id);
        return returnParticipant;
    }
}
