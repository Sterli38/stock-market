package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.config.StockMarketSettings;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
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
        Participant participant = new Participant();
        participant.setId(transactionRequest.getParticipantId());
        transaction.setOperationType(OperationType.DEPOSITING);
        transaction.setDate(new Date());
        transaction.setParticipant(participant);
        transaction.setGivenCurrency(transactionRequest.getGivenCurrency());
        transaction.setGivenAmount(transactionRequest.getGivenAmount());
        transaction.setCommission(calculateCommission(transactionRequest.getGivenAmount(), transactionRequest.getGivenCurrency()));
        log.info("Внесение средств: {}", transaction);
        Transaction saveTransaction = dao.saveTransaction(transaction);
        return saveTransaction;
    }

    public Transaction withdrawal(TransactionRequest transactionRequest) {
        if(!isOperationApplicable(transactionRequest.getGivenAmount(), transactionRequest.getGivenCurrency(), transactionRequest.getParticipantId())) {
            log.info("Невозможно вывести: {} в количестве {} у пользователя: {} недостаточно средств", transactionRequest.getGivenCurrency() , transactionRequest.getGivenCurrency(), transactionRequest.getParticipantId());
            throw new NotEnoughCurrencyException(transactionRequest.getGivenCurrency());
        }
        log.trace("У пользователя: {} хватает средств для проведения операции вывода", transactionRequest.getParticipantId());
        Transaction transaction = new Transaction();
        Participant participant = new Participant();
        participant.setId(transactionRequest.getParticipantId());
        transaction.setOperationType(OperationType.WITHDRAWAL);
        transaction.setDate(new Date());
        transaction.setParticipant(participant);
        transaction.setGivenCurrency(transactionRequest.getGivenCurrency());
        transaction.setGivenAmount(transactionRequest.getGivenAmount());
        transaction.setCommission(calculateCommission(transactionRequest.getGivenAmount(), transactionRequest.getGivenCurrency()));
        log.info("Сохранение транзакции: {}", transaction);
        Transaction saveTransaction = dao.saveTransaction(transaction);
        return saveTransaction;
    }

    public Transaction exchange(MakeExchangeRequest makeExchangeRequest) {
        String pair = makeExchangeRequest.getGivenCurrency() + makeExchangeRequest.getRequiredCurrency();
        if (!webCurrencyService.isValid(pair)) {
            log.warn("Пользователь {} ввёл неккоректную пару валют: {}" , makeExchangeRequest.getParticipantId(), pair);
            throw new CurrencyPairIsNotValidException(pair);
        }
        if(!isOperationApplicable(makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getParticipantId())) {
            log.warn("Невозможно обменять {} на {}, в количестве {} у пользователя: {} недостаточно средств", makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getRequiredCurrency(), makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getParticipantId());
            throw new NotEnoughCurrencyException(makeExchangeRequest.getGivenCurrency());
        }
        log.trace("Получена валидная пара: {}", pair);
        log.trace("У пользователя: {} хватает средств для проведения операции вывода", makeExchangeRequest.getParticipantId());

        Participant participant = new Participant();
        participant.setId(makeExchangeRequest.getParticipantId());
        Transaction transaction = new Transaction();
        transaction.setParticipant(participant);
        transaction.setGivenCurrency(makeExchangeRequest.getGivenCurrency());
        transaction.setGivenAmount(makeExchangeRequest.getGivenAmount());
        transaction.setReceivedCurrency(makeExchangeRequest.getRequiredCurrency());
        transaction.setOperationType(OperationType.EXCHANGE);
        transaction.setDate(new Date());
        transaction.setCommission(calculateCommission(transaction.getGivenAmount(), transaction.getGivenCurrency()));

        double receivedAmount = webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getGivenAmount() - transaction.getCommission(), transaction.getReceivedCurrency());
        transaction.setReceivedAmount(receivedAmount);

        log.info("Сохранение транзакции: {}", transaction);
        Transaction saveTransaction = dao.saveTransaction(transaction);
        return saveTransaction;
    }

    public double getBalanceByCurrency(Long participantId, String currency) {
        List<Transaction> transactions = dao.getTransactionsByCurrency(participantId, currency);

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
        log.trace("Сумма операций пополения: {}", depositingSum);

        for (Transaction value : replenishment) {
            replenishmentSum += value.getReceivedAmount();
        }
        log.trace("Сумма операций по покупке: {}", replenishmentSum);

        for (Transaction value : subtraction) {
            subtractionSum += value.getGivenAmount();
        }
        log.trace("Сумма операций по продаже: {}", subtractionSum);

        for (Transaction value : withdrawal) {
            withdrawalSum += value.getGivenAmount() - value.getCommission(); // возможно стоит оптимизировать
        }
        log.trace("Сумма операций вывода: {}", withdrawalSum);

        double balance = depositingSum + replenishmentSum - withdrawalSum - subtractionSum;
        log.trace("Баланс в валюте {} : {}", currency, balance);
        return balance;
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
            log.info("Расчёт комисии для валюты: {}, сумма комисии {}", currency, commission);
        } else {
            log.info("Комиссия к данной транзакции не применяется");
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
