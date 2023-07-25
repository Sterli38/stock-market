package com.example.stockmarket.dao;

import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();

            transaction.setId(rs.getLong("id"));
            transaction.setOperationType(OperationType.values()[(int) rs.getLong("operation_type_id") - 1]);
            transaction.setAmount(rs.getDouble("amount"));
            transaction.setCommission(rs.getDouble("commission"));
        return transaction;
    }
}
