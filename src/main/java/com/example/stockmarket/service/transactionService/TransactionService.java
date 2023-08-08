package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.config.StockMarketSettings;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.exception.CurrencyPairIsNotValidException;
import com.example.stockmarket.exception.NotEnoughCurrencyException;
import com.example.stockmarket.service.WebCurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionDao dao;
    private final WebCurrencyService webCurrencyService;
    private final StockMarketSettings stockMarketSettings;

    @Override
    public String toString() {
        return "TransactionService{" +
                "dao=" + dao +
                ", webCurrencyService=" + webCurrencyService +
                ", stockMarketSettings=" + stockMarketSettings +
                '}';
    }

    public Transaction depositing(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setOperationType(OperationType.DEPOSITING);
        transaction.setDate(new Date());
        transaction.setParticipantId(transactionRequest.getParticipantId());
        transaction.setGivenCurrency(transactionRequest.getGivenCurrency());
        transaction.setGivenAmount(transactionRequest.getGivenAmount());
        transaction.setCommission(calculateCommission(transactionRequest.getGivenAmount(), transactionRequest.getGivenCurrency()));
        return dao.saveTransaction(transaction);
    }

    public Transaction withdrawal(TransactionRequest transactionRequest) {
        if(!isOperationApplicable(transactionRequest.getGivenAmount(), transactionRequest.getGivenCurrency(), transactionRequest.getParticipantId())) {
            log.warn("Невозможно вывести: {} в количестве {} у пользователя: {} недостаточно средств", transactionRequest.getGivenCurrency() , transactionRequest.getGivenCurrency(), transactionRequest.getParticipantId());
            throw new NotEnoughCurrencyException(transactionRequest.getGivenCurrency());
        }
        Transaction transaction = new Transaction();
        transaction.setOperationType(OperationType.WITHDRAWAL);
        transaction.setDate(new Date());
        transaction.setParticipantId(transactionRequest.getParticipantId());
        transaction.setGivenCurrency(transactionRequest.getGivenCurrency());
        transaction.setGivenAmount(transactionRequest.getGivenAmount());
        transaction.setCommission(calculateCommission(transactionRequest.getGivenAmount(), transactionRequest.getGivenCurrency()));
        return dao.saveTransaction(transaction);
    }

    public Transaction exchange(MakeExchangeRequest makeExchangeRequest) {
        String pair = makeExchangeRequest.getGivenCurrency() + makeExchangeRequest.getRequiredCurrency();
        if (!webCurrencyService.isValid(pair)) {
            throw new CurrencyPairIsNotValidException(pair);
        }
        if(!isOperationApplicable(makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getParticipantId())) {
            log.warn("Невозможно обменять {} на {}, в количестве {} у пользователя: {} недостаточно средств", makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getRequiredCurrency(), makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getParticipantId());
            throw new NotEnoughCurrencyException(makeExchangeRequest.getGivenCurrency());
        }

        Transaction transaction = new Transaction();
        transaction.setParticipantId(makeExchangeRequest.getParticipantId());
        transaction.setGivenCurrency(makeExchangeRequest.getGivenCurrency());
        transaction.setGivenAmount(makeExchangeRequest.getGivenAmount());
        transaction.setReceivedCurrency(makeExchangeRequest.getRequiredCurrency());
        transaction.setOperationType(OperationType.EXCHANGE);
        transaction.setDate(new Date());
        transaction.setCommission(calculateCommission(transaction.getGivenAmount(), transaction.getGivenCurrency()));

        double receivedAmount = webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getGivenAmount() - transaction.getCommission(), transaction.getReceivedCurrency());
        transaction.setReceivedAmount(receivedAmount);

        Transaction saveTransaction = dao.saveTransaction(transaction);
        return saveTransaction;
    }

    public double getBalanceByCurrency(Long id, String currency) {
        List<Transaction> transactions = dao.getTransactionsByCurrency(id, currency);

        List<Transaction> depositing = new ArrayList<>();
        List<Transaction> replenishment = new ArrayList<>();
        List<Transaction> subtraction = new ArrayList<>();
        List<Transaction> withdrawal = new ArrayList<>();

        for(Transaction transaction: transactions) {
            if(transaction.getOperationType() == OperationType.DEPOSITING) {
                depositing.add(transaction);
            } else if (transaction.getOperationType() == OperationType.EXCHANGE && transaction.getReceivedCurrency().equals(currency)) {
                replenishment.add(transaction);
            } else if (transaction.getOperationType() == OperationType.EXCHANGE && transaction.getGivenCurrency().equals(currency)) {
                subtraction.add(transaction);
            } else if(transaction.getOperationType() == OperationType.WITHDRAWAL) {
                withdrawal.add(transaction);
            }
        }


        double depositingSum = 0;
        double replenishmentSum = 0;
        double subtractionSum = 0;
        double withdrawalSum = 0;

        for (Transaction value : depositing) {
            depositingSum += value.getGivenAmount() - value.getCommission();
        }

        for (Transaction value : replenishment) {
            replenishmentSum += value.getReceivedAmount();
        }

        for (Transaction value : subtraction) {
            subtractionSum += value.getGivenAmount();
        }

        for (Transaction value : withdrawal) {
            withdrawalSum += value.getGivenAmount() - value.getCommission(); // возможно стоит оптимизировать
        }

        return depositingSum + replenishmentSum - withdrawalSum - subtractionSum;
    }


    private double calculateCommission(double amount, String currency) {
        double commission = 0;
        double amountOfRub;

        if(!currency.equals(stockMarketSettings.getThresholdBaseCurrency())) {
            amountOfRub = webCurrencyService.convert(currency, amount, stockMarketSettings.getThresholdBaseCurrency());
        } else {
            amountOfRub = amount;
        }

        if (amountOfRub < stockMarketSettings.getThresholdOfCommissionUsage()) {
            commission = amount * stockMarketSettings.getCommissionPercent();
        }
        return commission;
    }

    /**
     * Проверка достаточности денежных средств для операции
     * @return
     */
    private boolean isOperationApplicable(double amount, String currency, long participantId) {
        double balance = getBalanceByCurrency(participantId, currency);
        return balance >= amount;
    }
}
