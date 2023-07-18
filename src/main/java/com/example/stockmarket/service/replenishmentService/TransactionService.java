package com.example.stockmarket.service.replenishmentService;

import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.WebCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionDao dao;
    private final WebCurrencyService webCurrencyService;

    public void replenishment(Transaction transaction) {
        transaction.setOperationType("Пополнение счёта");
        dao.add(transaction);
    }

    public void buy(Transaction transaction) {
        if (webCurrencyService.isValid(transaction.getReceivedCurrency() + transaction.getGivenCurrency())) {
            transaction.setAmount(webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getAmount(), transaction.getReceivedCurrency()));
            transaction.setOperationType("Покупка валюты на бирже");
            dao.buy(transaction);
        }
    }

    public void sell(Transaction transaction) {
        if (webCurrencyService.isValid(transaction.getReceivedCurrency() + transaction.getGivenCurrency())) {
            transaction.setAmount(webCurrencyService.convert(transaction.getReceivedCurrency(), transaction.getAmount(), transaction.getGivenCurrency()));
            transaction.setOperationType("Продажа валюты на бирже");
            dao.sell(transaction);
        }
    }

    public double getBalanceByCurrency(Transaction transaction) {
        List<Transaction> transactions = dao.getBalanceByCurrency(transaction);

        List<Transaction> transactions1 = transactions.stream()
                .filter(i -> Objects.equals(i.getOperationType(), "Пополнение счёта"))
                .toList(); // Пополнение в данной валюте

        List<Transaction> transactions2 = transactions.stream()
                .filter(i -> Objects.equals(i.getOperationType(), "Покупка валюты на бирже"))
                .toList(); // Покупка данной валюты

        List<Transaction> transactions3 = transactions.stream()
                .filter(i -> Objects.equals(i.getOperationType(), "Продажа валюты на бирже"))
                .toList(); // Продажа валюты

        double sum = 0;
        double sum1 = 0;
        double sum2 = 0;

        for (Transaction value : transactions1) {
            sum += value.getAmount();
        }

        for (Transaction value : transactions2) {
            sum1 += value.getAmount();
        }

        for (Transaction value : transactions3) {
            sum2 += value.getAmount();
        }

        return sum + sum1 - sum2;
    }
}
