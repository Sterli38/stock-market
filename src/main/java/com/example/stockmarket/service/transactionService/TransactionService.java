package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.OperationType;
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

    public void depositing(Transaction transaction) {
//        Long operationTypeId = dao.findTypeById(OperationType.DEPOSITING.name());
//        transaction.setOperationTypeId(operationTypeId);
        transaction.setAmount(commissionCalculation(transaction.getAmount(), transaction.getReceivedCurrency()));
        transaction.setOperationTypeId(dao.findTypeById(OperationType.DEPOSITING.name()));

        dao.saveTransaction(transaction);
    }

    public void buy(Transaction transaction) {
        if (webCurrencyService.isValid(transaction.getReceivedCurrency() + transaction.getGivenCurrency())) {
            transaction.setOperationTypeId(dao.findTypeById(OperationType.BUYING.name()));

            double amountReceivedCurrency = webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getAmount(), transaction.getReceivedCurrency());

            transaction.setAmount(commissionCalculation(amountReceivedCurrency, transaction.getReceivedCurrency()));
            dao.saveTransaction(transaction);
        }
    }

    public void sell(Transaction transaction) {
        if (webCurrencyService.isValid(transaction.getReceivedCurrency() + transaction.getGivenCurrency())) {
            transaction.setOperationTypeId(dao.findTypeById(OperationType.SELLING.name()));

            double amountReceivedCurrency = webCurrencyService.convert(transaction.getReceivedCurrency(), transaction.getAmount(), transaction.getGivenCurrency());

            transaction.setAmount(commissionCalculation(amountReceivedCurrency, transaction.getReceivedCurrency()));
            dao.saveTransaction(transaction);
        }
    }

    private double commissionCalculation(double amount, String currency) {
        double amountOfRub = webCurrencyService.convert(currency, amount, "RUB");

        if (amountOfRub < 5000) {
            double commission = 0.05;

            return amount - (amount * commission);
        }
        return amount;
    }

    public double getBalanceByCurrency(Transaction transaction) {
        List<Transaction> transactions = dao.getBalanceByCurrency(transaction);

        List<Transaction> transactions1 = transactions.stream()
                .filter(i -> i.getOperationTypeId() == 1)
                .toList();

        List<Transaction> transactions2 = transactions.stream()
                .filter(i -> i.getOperationTypeId() == 2)
                .toList();

        List<Transaction> transactions3 = transactions.stream()
                .filter(i -> i.getOperationTypeId() == 3)
                .toList();

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
