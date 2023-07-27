package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Transaction;

import java.util.List;

public interface TransactionDao {

    Transaction saveTransaction(Transaction transaction);

    List<Transaction> getBalanceByCurrency(Long id, String currency);
}
