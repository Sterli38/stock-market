package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();

            transaction.setId(rs.getLong("id"));
            transaction.setOperationTypeId(rs.getLong("operation_type_id"));
            transaction.setDate(rs.getTimestamp("date"));
            transaction.setAmount(rs.getDouble("amount"));
            transaction.setParticipantId(rs.getLong("participant_id"));
            transaction.setReceivedCurrency(rs.getString("received_currency"));
            transaction.setGivenCurrency(rs.getString("given_currency"));
            transaction.setCommission(rs.getDouble("commission"));
        return transaction;
    }
}
