package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.config.StockMarketSettings;
import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.exception.CurrencyPairIsNotValidException;
import com.example.stockmarket.service.WebCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionDao dao;
    private final WebCurrencyService webCurrencyService;
    private final StockMarketSettings stockMarketSettings;

    public Transaction depositing(Transaction transaction) {
        transaction.setOperationType(OperationType.DEPOSITING);
        transaction.setCommission(calculateCommission(transaction.getAmount(), transaction.getReceivedCurrency()));
        return dao.saveTransaction(transaction);
    }

    public Transaction withdrawal(Transaction transaction) {
        transaction.setOperationType(OperationType.WITHDRAWAL);
        transaction.setCommission(calculateCommission(transaction.getAmount(), transaction.getGivenCurrency()));
        return dao.saveTransaction(transaction);
    }

    public Transaction exchange(Transaction transaction) {
        String pair = transaction.getReceivedCurrency() + transaction.getGivenCurrency();
        transaction.setOperationType(OperationType.EXCHANGE);
        if (!webCurrencyService.isValid(pair)) {
            throw new CurrencyPairIsNotValidException(pair);
        }

        double amountReceivedCurrency = webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getAmount(), transaction.getReceivedCurrency());

        transaction.setCommission(calculateCommission(amountReceivedCurrency, transaction.getReceivedCurrency()));
        transaction.setAmount(amountReceivedCurrency);
        return dao.saveTransaction(transaction);
    }

    public double getBalanceByCurrency(Long id, String currency) {
        List<Transaction> transactions = dao.getBalanceByCurrency(id, currency);

        List<Transaction> depositing = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.DEPOSITING)
                .toList();

        List<Transaction> replenishment = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.EXCHANGE)
                .filter(i -> Objects.equals(i.getReceivedCurrency(), currency))
                .toList();

        List<Transaction> subtraction = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.EXCHANGE)
                .filter(i -> Objects.equals(i.getGivenCurrency(), currency))
                .toList();


        List<Transaction> subtraction1 = new ArrayList<>();
        for(Transaction element: subtraction) {
            element.setAmount(webCurrencyService.convert(element.getReceivedCurrency(), element.getAmount(), element.getGivenCurrency()));
            element.setCommission(calculateCommission(element.getAmount(), element.getGivenCurrency()));
            subtraction1.add(element);
        }

        List<Transaction> withdrawal = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.WITHDRAWAL)
                .toList();

        double depositingSum = 0;
        double replenishmentSum = 0;
        double subtractionSum = 0;
        double withdrawalSum = 0;

        for (Transaction value : depositing) {
            depositingSum += value.getAmount() - value.getCommission();
        }

        for (Transaction value : replenishment) {
            replenishmentSum += value.getAmount() - value.getCommission();
        }

        for (Transaction value : subtraction1) {
            subtractionSum += value.getAmount() - value.getCommission();
        }

        for (Transaction value : withdrawal) {
            withdrawalSum += value.getAmount() - value.getCommission();
        }

        return depositingSum + replenishmentSum - (withdrawalSum + subtractionSum);
    }

    private double calculateCommission(double amount, String currency) {
        double commission = 0;
        double amountOfRub;
        
        if(!currency.equals(stockMarketSettings.getThresholdOfCommissionUsage())) {
            amountOfRub = webCurrencyService.convert(currency, amount, stockMarketSettings.getThresholdBaseCurrency());
        } else {
            amountOfRub = amount;
        }

        if (amountOfRub < stockMarketSettings.getThresholdOfCommissionUsage()) {
            commission = amount * stockMarketSettings.getCommissionPercent();
        }
        return commission;
    }
}
