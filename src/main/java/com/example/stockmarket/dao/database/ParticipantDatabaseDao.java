package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.dao.ParticipantMapper;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
//        for(Role value: participant.getRoles()) {
//            String roleSql = "INSERT INTO participant_to_role(participant_id, role_id) values(?, (SELECT role.id FROM role WHERE role = ?))";
//            jdbcTemplate.update(roleSql, participantId, value.name());
//        }
//        - Кто должен присваивать enabled и roles Участнику ?
        Participant returnParticipant = getParticipantById(participantId);
        return returnParticipant;
    }

    @Override
    @Nullable
    public Participant getParticipantById(long id) {
//        String sql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled, role.role as role_name FROM participant JOIN participant_to_role on participant.id = participant_to_role.participant_id JOIN role on role.id = participant_to_role.role_id WHERE participant.id = ?";
        String participantSql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled FROM participant WHERE participant.id = ?";
        String roleSql = "SELECT role.role FROM role JOIN participant_to_role on role.id = participant_to_role.role_id WHERE participant_to_role.participant_id = ?";
        try {
            Participant participant = jdbcTemplate.queryForObject(participantSql, new ParticipantMapper(), id);
            List<Role> list = jdbcTemplate.queryForList(roleSql, Role.class, id);
            Set<Role> participantRoles = new HashSet<Role>(list);
            participant.setRoles(participantRoles);
            return participant;
//            return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Participant getParticipantByName(String name) {
//        String sql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled, role.role as role_name FROM participant JOIN participant_to_role on participant.id = participant_to_role.participant_id JOIN role on role.id = participant_to_role.role_id WHERE participant.name = ?";
        String participantSql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled FROM participant WHERE participant.name = ?";
        String roleSql = "SELECT role.role FROM role JOIN participant_to_role on role.id = participant_to_role.role_id WHERE participant_to_role.participant_id = ?";
        try {
            Participant participant = jdbcTemplate.queryForObject(participantSql, new ParticipantMapper(), name);
            List<Role> list = jdbcTemplate.queryForList(roleSql, Role.class, participant.getId());
            Set<Role> participantRoles = new HashSet<Role>(list);
            participant.setRoles(participantRoles);
            return participant;
//             return jdbcTemplate.queryForObject(sql, new ParticipantMapper(), name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Participant editParticipant(Participant participant) {
        if(participant.getRoles() != null ) {
            String roleSql = "UPDATE participant_to_role SET role_id = (SELECT role.id WHERE role = ?), participant_id = ?";
            jdbcTemplate.update(roleSql, participant.getRoles(), participant.getId());
        }
        String participantSql = "UPDATE participant SET name = ?, password = ?, enabled = ?, creation_date = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(participantSql, participant.getName(), participant.getRoles().toString(), participant.getCreationDate(), participant.getPassword(), participant.getId());
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
