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
        String sql = "INSERT INTO history (operation_type_id, date, amount, participant_id, received_currency, given_currency, commission)" +
                "values ((SELECT id FROM operation_type WHERE type = ?), ?, ?, ?, ?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, transaction.getOperationType().name());
            ps.setTimestamp(2, new Timestamp(transaction.getDate().getTime()));
            ps.setDouble(3, transaction.getAmount());
            ps.setLong(4, transaction.getParticipantId());
            ps.setString(5, transaction.getReceivedCurrency());
            ps.setString(6, transaction.getGivenCurrency());
            ps.setDouble(7, transaction.getCommission());
            return ps;
        }, holder);
        transaction.setId(holder.getKey().longValue());
        return transaction;
    }

    @Override
    public List<Transaction> getBalanceByCurrency(Long id, String currency) {
//        String sql = "SELECT id, operation_type_id, amount, commission FROM history " +
//                "WHERE participant_id = ? and (received_currency = ? or given_currency = ?)";
//        return jdbcTemplate.query(sql, new TransactionMapper(), id, currency, currency);
        String sql = "SELECT history.id, operation_type.type, amount, commission, received_currency, given_currency FROM history JOIN operation_type on history.operation_type_id = operation_type.id WHERE participant_id = ? and (received_currency = ? or given_currency = ?)";
        return jdbcTemplate.query(sql, new TransactionMapper(), id, currency, currency);
 }
}
