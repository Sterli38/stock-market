package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.entity.TransactionFilter;

import java.util.List;

public interface TransactionDao {

    Transaction saveTransaction(Transaction transaction);

    List<Transaction> getTransactionsByCurrency(Long id, String currency);

    List<Transaction> getTransactionsByFilter(TransactionFilter transactionFilter);
}
