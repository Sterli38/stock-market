package com.example.stockmarket.dao.mapper;

import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();

        transaction.setId(rs.getLong("transaction_id"));
        transaction.setOperationType(OperationType.valueOf(rs.getString("type")));
        transaction.setDate(rs.getTimestamp("date"));
        transaction.setReceivedCurrency(rs.getString("received_currency"));
        transaction.setReceivedAmount(rs.getDouble("received_amount"));
        transaction.setGivenCurrency(rs.getString("given_currency"));
        transaction.setGivenAmount(rs.getDouble("given_amount"));
        transaction.setCommission(rs.getDouble("commission"));

        return transaction;
    }
}
