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
        ParticipantMapper participantMapper = new ParticipantMapper();
        Participant participant = participantMapper.mapRow(rs, rowNum);

        Transaction transaction = new Transaction();

        transaction.setId(rs.getLong("transaction_id"));
        transaction.setOperationType(OperationType.valueOf(rs.getString("type")));
        transaction.setDate(rs.getDate("date"));
        transaction.setReceivedCurrency(rs.getString("received_currency"));
        transaction.setReceivedAmount(rs.getDouble("received_amount"));
        transaction.setGivenCurrency(rs.getString("given_currency"));
        transaction.setGivenAmount(rs.getDouble("given_amount"));
        transaction.setParticipant(participant);
        transaction.setCommission(rs.getDouble("commission"));

        return transaction;
    }
}
