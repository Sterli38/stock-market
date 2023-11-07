package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.dao.mapper.ParticipantMapper;
import com.example.stockmarket.dao.mapper.RoleMapper;
import com.example.stockmarket.dao.mapper.TransactionMapper;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.entity.TransactionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TransactionDatabaseDao implements TransactionDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transaction (operation_type_id, date, received_amount, given_amount, participant_id, received_currency, given_currency, commission)" +
                "values ((SELECT id FROM operation_type WHERE type = ?), ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, transaction.getOperationType().name());
            ps.setTimestamp(2, new Timestamp(transaction.getDate().getTime()));
            ps.setObject(3, transaction.getReceivedAmount());
            ps.setObject(4, transaction.getGivenAmount());
            ps.setLong(5, transaction.getParticipant().getId());
            ps.setString(6, transaction.getReceivedCurrency());
            ps.setString(7, transaction.getGivenCurrency());
            ps.setDouble(8, transaction.getCommission());
            return ps;
        }, holder);
        transaction.setId(holder.getKey().longValue());
        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsByCurrency(Long participantId, String currency) {
        String sql = "SELECT participant.id as participant_id, participant.name as participant_name, participant.creation_date, participant.password, participant.enabled, string_agg(role.role_name, ',' ORDER BY role.role_name) as role_name, transaction.id as transaction_id, operation_type.type, received_currency, received_amount, given_currency, given_amount, date, commission " +
                "FROM transaction " +
                "JOIN participant on transaction.participant_id = participant.id " +
                "JOIN operation_type on operation_type.id = transaction.operation_type_id " +
                "JOIN participant_to_role on participant_to_role.participant_id = participant.id " +
                "JOIN role on participant_to_role.role_id = role.id " +
                "WHERE participant.id = ? and (received_currency = ? or given_currency = ?) " +
                "GROUP BY participant.id, participant.name, participant.creation_date, participant.password, participant.enabled, transaction.id, operation_type.type, received_currency, received_amount, given_currency, given_amount, date, commission";
        return jdbcTemplate.query(sql, new TransactionMapper(), participantId, currency, currency);
    }

    public List<Transaction> getTransactionsByFilter(TransactionFilter transactionFilter) {
        Map<String, Object> values = new HashMap<>();
        SqlBuilder participantSqlBuilder = new SqlBuilder();
        SqlBuilder rolesSqlBuilder = new SqlBuilder();
        SqlBuilder transactionSqlBuilder = new SqlBuilder();

        String participantSql = participantSqlBuilder.select("participant.id as participant_id, participant.name as participant_name, participant.password, participant.creation_date, participant.enabled")
                .from("participant")
                .where("participant.id = ?")
                .build();

        String roleSql = rolesSqlBuilder.select("role.id as role_id, role.role_name")
                .from("role")
                .join("participant_to_role")
                .on("role.id = participant_to_role.role_id")
                .where("participant_to_role.participant_id = ?")
                .build();

            Participant participant = jdbcTemplate.queryForObject(participantSql, new ParticipantMapper(), transactionFilter.getParticipantId());
            List<Role> list = jdbcTemplate.query(roleSql, new RoleMapper(), transactionFilter.getParticipantId());
            Set<Role> participantRoles = new HashSet<Role>(list);
            participant.setRoles(participantRoles);

        transactionSqlBuilder.select("transaction.id as transaction_id, operation_type.type, received_currency, received_amount, given_currency, given_amount, date, commission")
                .from("transaction")
                .join("operation_type").on("operation_type.id = transaction.operation_type_id");

        if ((transactionFilter.getReceivedCurrencies() != null && transactionFilter.getGivenCurrencies() != null) && transactionFilter.getReceivedCurrencies().equals(transactionFilter.getGivenCurrencies())) {
            transactionSqlBuilder.where("(received_currency IN (:currency) or given_currency IN (:currency))");
            values.put("currency", transactionFilter.getReceivedCurrencies());
        } else {
            if (transactionFilter.getReceivedCurrencies() != null) {
                transactionSqlBuilder.where("received_currency IN (:receivedCurrency)");
                values.put("receivedCurrency", transactionFilter.getReceivedCurrencies());
            }
            if (transactionFilter.getGivenCurrencies() != null) {
                transactionSqlBuilder.where("given_currency IN (:givenCurrency)");
                values.put("givenCurrency", transactionFilter.getGivenCurrencies());
            }
        }
        if ((transactionFilter.getOperationType() != null)) {
            transactionSqlBuilder.where("operation_type.id = (SELECT id FROM operation_type WHERE type = :operationType)");
            values.put("operationType", transactionFilter.getOperationType().name());
        }
        if (transactionFilter.getAfter() != null) {
            transactionSqlBuilder.where("date >= :after");
            values.put("after", transactionFilter.getAfter());
        }
        if (transactionFilter.getBefore() != null) {
            transactionSqlBuilder.where("date <= :before");
            values.put("before", transactionFilter.getBefore());
        }
        if (transactionFilter.getReceivedMinAmount() != null) {
            transactionSqlBuilder.where("received_amount >= :receivedMinAmount");
            values.put("receivedMinAmount", transactionFilter.getReceivedMinAmount());
        }
        if (transactionFilter.getReceivedMaxAmount() != null) {
            transactionSqlBuilder.where("received_amount <= :receivedMaxAmount");
            values.put("receivedMaxAmount", transactionFilter.getReceivedMaxAmount());
        }
        if (transactionFilter.getGivenMinAmount() != null) {
            transactionSqlBuilder.where("given_amount >= :givenMinAmount");
            values.put("givenMinAmount", transactionFilter.getGivenMinAmount());
        }
        if (transactionFilter.getGivenMaxAmount() != null) {
            transactionSqlBuilder.where("given_amount <= :givenMaxAmount");
            values.put("givenMaxAmount", transactionFilter.getGivenMaxAmount());
        }

        transactionSqlBuilder.where("participant_id = :participantId");
        values.put("participantId", transactionFilter.getParticipantId());

        String transactionSql = transactionSqlBuilder.build();

        List<Transaction> transactions = namedParameterJdbcTemplate.query(transactionSql, values, new TransactionMapper());

        for(Transaction value: transactions) {
            value.setParticipant(participant);
        }

        return transactions;
    }
}
