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

    @Override
    public void saveTransaction(Transaction transaction) { // у нас нет типа операции у нас теперь id типа операции
        String sql = "INSERT INTO history (operation_type_id, date, amount, participant_id, received_currency, given_currency, commission)" +
                " values(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, transaction.getOperationTypeId(), transaction.getDate(), transaction.getAmount(), transaction.getParticipantId(),
                transaction.getReceivedCurrency(), transaction.getGivenCurrency(), transaction.getCommission());
    }

    @Override
    public Long findTypeById(String type) {
        String sql = "SELECT id FROM operation_type WHERE type = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, type);
}

    @Override
    public List<Transaction> getBalanceByCurrency(Transaction transaction) {
        String sql = "SELECT * FROM history WHERE participant_id = ? and received_currency = ? or given_currency = ?";
        return jdbcTemplate.query(sql, new TransactionMapper(), transaction.getParticipantId(), transaction.getReceivedCurrency(), transaction.getGivenCurrency());
    }
}
