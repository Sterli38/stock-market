package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.dao.mapper.ParticipantMapper;
import com.example.stockmarket.dao.mapper.TransactionMapper;
import com.example.stockmarket.entity.OperationType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder
                .select("participant.id as participant_id, participant.name as participant_name, participant.creation_date, participant.password, participant.enabled, string_agg(role.role_name, ',' ORDER BY role.role_name) as role_name, transaction.id as transaction_id, operation_type.type, received_currency, received_amount, given_currency, given_amount, date, commission")
                .from("transaction JOIN participant on transaction.participant_id = participant.id JOIN operation_type on operation_type.id = transaction.operation_type_id JOIN participant_to_role on participant_to_role.participant_id = participant.id JOIN role on participant_to_role.role_id = role.id ");
        if (transactionFilter.getOperationType() != null) {
            sqlBuilder.where("operation_type.id = (SELECT id FROM operation_type WHERE type = :operationType)");
            values.put("operationType", transactionFilter.getOperationType().name());
        } else {
            if (transactionFilter.getReceivedCurrencies() != null && transactionFilter.getGivenCurrencies() != null) {
                if (transactionFilter.getReceivedCurrencies().equals(transactionFilter.getGivenCurrencies())) {
                    sqlBuilder.where("(received_currency = :currency or given_currency = :currency)");
                    values.put("currency", transactionFilter.getReceivedCurrencies());
                }else {
                    if (transactionFilter.getReceivedCurrencies() != null) {
                        sqlBuilder.where("received_currency IN (:receivedCurrency)");
                        values.put("receivedCurrency", transactionFilter.getReceivedCurrencies());
                    }
                    if (transactionFilter.getGivenCurrencies() != null) {
                        sqlBuilder.where("given_currency IN (:givenCurrency)");
                        values.put("givenCurrency", transactionFilter.getGivenCurrencies());
                    }
                }
            }
        }
        if (transactionFilter.getAfter() != null) {
            sqlBuilder.where("date >= :after");
            values.put("after", transactionFilter.getAfter());
        }
        if (transactionFilter.getBefore() != null) {
            sqlBuilder.where("date <= :before");
            values.put("before", transactionFilter.getBefore());
        }
//        if (transactionFilter.getReceivedCurrencies() != null) {
//            sqlBuilder.where("received_currency IN (:receivedCurrency)");
//            values.put("receivedCurrency", transactionFilter.getReceivedCurrencies());
//        }
        if (transactionFilter.getReceivedMinAmount() != null) {
            sqlBuilder.where("received_amount >= :receivedMinAmount");
            values.put("receivedMinAmount", transactionFilter.getReceivedMinAmount());
        }
        if (transactionFilter.getReceivedMaxAmount() != null) {
            sqlBuilder.where("received_amount <= :receivedMaxAmount");
            values.put("receivedMaxAmount", transactionFilter.getReceivedMaxAmount());
        }
//        if (transactionFilter.getGivenCurrencies() != null) {
//            sqlBuilder.where("given_currency IN (:givenCurrency)");
//            values.put("givenCurrency", transactionFilter.getGivenCurrencies());
//        }
        if (transactionFilter.getGivenMinAmount() != null) {
            sqlBuilder.where("given_amount >= :givenMinAmount");
            values.put("givenMinAmount", transactionFilter.getGivenMinAmount());
        }
        if (transactionFilter.getGivenMaxAmount() != null) {
            sqlBuilder.where("given_amount <= :givenMaxAmount");
            values.put("givenMaxAmount", transactionFilter.getGivenMaxAmount());
        }

        sqlBuilder.where("participant.id = :participantId");
        values.put("participantId", transactionFilter.getParticipantId());
        sqlBuilder.build();

        sqlBuilder.condition(" GROUP BY ", "participant.id, participant.name, participant.creation_date, participant.password, participant.enabled, transaction.id, operation_type.type, received_currency, received_amount, given_currency, given_amount, date, commission");

        String sql = sqlBuilder.getSql();
        List<Transaction> transactions = namedParameterJdbcTemplate.query(sql, values, new TransactionMapper());
        return transactions;
    }
}
