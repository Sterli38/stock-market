package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.dao.TransactionMapper;
import com.example.stockmarket.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionDatabaseDao implements TransactionDao {
    private final JdbcTemplate jdbcTemplate;

    public void add(Transaction transaction) {
        String sql = "INSERT INTO history (operation_type, date,amount, participant_id, received_currency, commission)" +
                " values(?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, transaction.getOperationType(),transaction.getDate(), transaction.getAmount(), transaction.getParticipantId(),
                transaction.getReceivedCurrency(), transaction.getCommission());
    }

    public void buy(Transaction transaction) {
        String sql = "INSERT INTO history (operation_type, date, amount, participant_id, received_currency, given_currency, commission) " +
                "values(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, transaction.getOperationType(), transaction.getDate(), transaction.getAmount(),
                transaction.getParticipantId(), transaction.getReceivedCurrency(), transaction.getGivenCurrency(), transaction.getCommission());
    }

    public void sell(Transaction transaction) {
        String sql = "INSERT INTO history (operation_type, date, amount, participant_id, given_currency, received_currency, commission) " +
                "values(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, transaction.getOperationType(), transaction.getDate(), transaction.getAmount(),
                transaction.getParticipantId(), transaction.getGivenCurrency(), transaction.getReceivedCurrency(), transaction.getCommission());
    }

    public List<Transaction> getBalanceByCurrency(Transaction transaction) {
        String sql = "SELECT * FROM history WHERE participant_id = ? and received_currency = ? or given_currency = ?";
        return jdbcTemplate.query(sql, new TransactionMapper(), transaction.getParticipantId(), transaction.getReceivedCurrency(), transaction.getGivenCurrency());
    }
}
