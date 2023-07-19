package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Transaction;

import java.util.List;

public interface TransactionDao {

    void saveTransaction(Transaction transaction);

    Long findTypeById(String type);

    List<Transaction> getBalanceByCurrency(Transaction transaction);
}
