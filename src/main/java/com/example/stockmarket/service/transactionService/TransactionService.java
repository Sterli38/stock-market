package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.config.StockMarketSettings;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
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

    public Transaction depositing(MakeDepositingRequest makeDepositingRequest) {
        Transaction transaction = new Transaction();
        Participant participant = new Participant();
        participant.setId(makeDepositingRequest.getParticipantId());
        transaction.setOperationType(OperationType.DEPOSITING);
        transaction.setDate(new Date());
        transaction.setParticipant(participant);
        transaction.setReceivedCurrency(makeDepositingRequest.getReceivedCurrency());
        transaction.setReceivedAmount(makeDepositingRequest.getReceivedAmount());
        transaction.setCommission(calculateCommission(transaction.getReceivedAmount(), transaction.getReceivedCurrency()));
        return dao.saveTransaction(transaction);
    }

    public Transaction withdrawal(MakeWithdrawalRequest makeWithdrawalRequest) {
        if(!isOperationApplicable(makeWithdrawalRequest.getGivenAmount(), makeWithdrawalRequest.getGivenCurrency(), makeWithdrawalRequest.getParticipantId())) {
            log.warn("Невозможно вывести: {} в количестве {} у пользователя: {} недостаточно средств", makeWithdrawalRequest.getGivenCurrency() , makeWithdrawalRequest.getGivenCurrency(), makeWithdrawalRequest.getParticipantId());
            throw new NotEnoughCurrencyException(makeWithdrawalRequest.getGivenCurrency());
        }
        Transaction transaction = new Transaction();
        Participant participant = new Participant();
        participant.setId(makeWithdrawalRequest.getParticipantId());
        transaction.setOperationType(OperationType.WITHDRAWAL);
        transaction.setDate(new Date());
        transaction.setParticipant(participant);
        transaction.setGivenCurrency(makeWithdrawalRequest.getGivenCurrency());
        transaction.setGivenAmount(makeWithdrawalRequest.getGivenAmount());
        transaction.setCommission(calculateCommission(makeWithdrawalRequest.getGivenAmount(), makeWithdrawalRequest.getGivenCurrency()));
        return dao.saveTransaction(transaction);
    }

    public Transaction exchange(MakeExchangeRequest makeExchangeRequest) {
        String pair = makeExchangeRequest.getGivenCurrency() + makeExchangeRequest.getReceivedCurrency();
        if (!webCurrencyService.isValid(pair)) {
            throw new CurrencyPairIsNotValidException(pair);
        }
        if(!isOperationApplicable(makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getParticipantId())) {
            log.warn("Невозможно обменять {} на {}, в количестве {} у пользователя: {} недостаточно средств", makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getReceivedCurrency(), makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getParticipantId());
            throw new NotEnoughCurrencyException(makeExchangeRequest.getGivenCurrency());
        }
        Participant participant = new Participant();
        participant.setId(makeExchangeRequest.getParticipantId());
        Transaction transaction = new Transaction();
        transaction.setParticipant(participant);
        transaction.setGivenCurrency(makeExchangeRequest.getGivenCurrency());
        transaction.setGivenAmount(makeExchangeRequest.getGivenAmount());
        transaction.setReceivedCurrency(makeExchangeRequest.getReceivedCurrency());
        transaction.setOperationType(OperationType.EXCHANGE);
        transaction.setDate(new Date());
        transaction.setCommission(calculateCommission(transaction.getGivenAmount(), transaction.getGivenCurrency()));

        double receivedAmount = webCurrencyService.convert(transaction.getGivenCurrency(), transaction.getGivenAmount() - transaction.getCommission(), transaction.getReceivedCurrency());
        transaction.setReceivedAmount(receivedAmount);

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


//        List<Transaction> depositing = transactions.stream()
//                .filter(i -> i.getOperationType() == OperationType.DEPOSITING)
//                .toList();
//
//        List<Transaction> replenishment = transactions.stream()
//                .filter(i -> i.getOperationType() == OperationType.EXCHANGE)
//                .filter(i -> Objects.equals(i.getReceivedCurrency(), currency))
//                .toList();
//
//        List<Transaction> subtraction = transactions.stream()
//                .filter(i -> i.getOperationType() == OperationType.EXCHANGE)
//                .filter(i -> Objects.equals(i.getGivenCurrency(), currency))
//                .toList();
//
//        List<Transaction> withdrawal = transactions.stream()
//                .filter(i -> i.getOperationType() == OperationType.WITHDRAWAL)
//                .toList();

        double depositingSum = 0;
        double replenishmentSum = 0;
        double subtractionSum = 0;
        double withdrawalSum = 0;

        for (Transaction value : depositing) {
            depositingSum += value.getReceivedAmount() - value.getCommission();
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