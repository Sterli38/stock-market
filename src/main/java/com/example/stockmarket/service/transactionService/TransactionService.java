package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.config.StockMarketSettings;
import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.exception.CurrencyPairIsNotValidException;
import com.example.stockmarket.service.WebCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Transaction buy(Transaction transaction) {
        String pair = transaction.getReceivedCurrency() + transaction.getGivenCurrency();
        if (!webCurrencyService.isValid(pair)) {
            throw new CurrencyPairIsNotValidException(pair);
        }
        transaction.setOperationType(OperationType.BUYING);

        double amountReceivedCurrency = webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getAmount(), transaction.getReceivedCurrency());

        transaction.setCommission(calculateCommission(amountReceivedCurrency, transaction.getReceivedCurrency()));
        transaction.setAmount(amountReceivedCurrency);
        return dao.saveTransaction(transaction);
    }

    public Transaction sell(Transaction transaction) {
        String pair = transaction.getReceivedCurrency() + transaction.getGivenCurrency();
        if (!webCurrencyService.isValid(pair)) {
         throw new CurrencyPairIsNotValidException(pair);
        }
            transaction.setOperationType(OperationType.SELLING);

//            double amountReceivedCurrency = webCurrencyService.convert(transaction.getReceivedCurrency(), transaction.getAmount(), transaction.getGivenCurrency());

            transaction.setCommission(calculateCommission(transaction.getAmount(), transaction.getGivenCurrency()));
            return dao.saveTransaction(transaction);
    }

    public double getBalanceByCurrency(Transaction transaction) {
        List<Transaction> transactions = dao.getBalanceByCurrency(transaction);

        List<Transaction> transactions1 = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.DEPOSITING)
                .toList();

        List<Transaction> transactions2 = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.BUYING)
                .toList();

        List<Transaction> transactions3 = transactions.stream()
                .filter(i -> i.getOperationType() == OperationType.SELLING)
                .toList();

        double sum = 0;
        double sum1 = 0;
        double sum2 = 0;

        for (Transaction value : transactions1) {
            sum += value.getAmount() - value.getCommission();
        }

        for (Transaction value : transactions2) {
            sum1 += value.getAmount() - value.getCommission();
        }

        for (Transaction value : transactions3) {
            sum2 += value.getAmount() - value.getCommission();
        }

        return sum + sum1 - sum2;
    }

    private double calculateCommission(double amount, String currency) {
        double commission = 0;
        double amountOfRub;
        String transferCurrency = "RUB";
        if(!currency.equals(transferCurrency)) {
            amountOfRub = webCurrencyService.convert(currency, amount, transferCurrency);
        } else {
            amountOfRub = amount;
        }

        if (amountOfRub < stockMarketSettings.getCommissionThreshold()) {
            commission = amount * stockMarketSettings.getCommission();
        }
        return commission;
    }
}
