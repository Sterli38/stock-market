package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Transaction;

import java.util.List;

public interface TransactionDao {

    void add(Transaction transaction);

    void buy(Transaction transaction);

    void sell(Transaction transaction);

    List<Transaction> getBalanceByCurrency(Transaction transaction);
}
