package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.dao.TransactionMapper;
import com.example.stockmarket.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionDatabaseDao implements TransactionDao {
    private final JdbcTemplate jdbcTemplate;

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
        String sql = "SELECT participant.id, participant.name, participant.creation_date, operation_type.type, received_amount, given_amount, commission, received_currency, given_currency FROM transaction" +
                " JOIN operation_type on transaction.operation_type_id = operation_type.id" +
                " JOIN participant on transaction.participant_id = participant.id " +
                " WHERE participant_id = ? and (received_currency = ? or given_currency = ?)";
        return jdbcTemplate.query(sql, new TransactionMapper(), participantId, currency, currency);
    }
}
