package com.example.stockmarket.service.transactionService;

import com.example.stockmarket.config.StockMarketSettings;
import com.example.stockmarket.controller.request.transactionRequest.GetTransactionsRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
import com.example.stockmarket.dao.ParticipantDao;
import com.example.stockmarket.dao.TransactionDao;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.entity.TransactionFilter;
import com.example.stockmarket.exception.CurrencyIsNotValidException;
import com.example.stockmarket.exception.CurrencyPairIsNotValidException;
import com.example.stockmarket.exception.NoCurrencyForAmountException;
import com.example.stockmarket.exception.NotEnoughCurrencyException;
import com.example.stockmarket.exception.ParticipantNotFoundException;
import com.example.stockmarket.service.WebCurrencyService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final ParticipantDao participantDao;
    private final TransactionDao transactionDao;
    private final WebCurrencyService webCurrencyService;
    private final StockMarketSettings stockMarketSettings;
    private final MeterRegistry meterRegistry;

    public Transaction depositing(MakeDepositingRequest makeDepositingRequest) {
        Counter unsuccesfullDepositingsCounter = meterRegistry.counter("webCurrencyService.transactionService.unsuccessfulDeposits");
        if (!isParticipantExists(makeDepositingRequest.getParticipantId())) {
            unsuccesfullDepositingsCounter.increment();
            log.info("Невозможно произвести пополнение: участник с id [{}] не найден", makeDepositingRequest.getParticipantId());
            throw new ParticipantNotFoundException(makeDepositingRequest.getParticipantId());
        }
        if(!webCurrencyService.isValidCurrency(makeDepositingRequest.getReceivedCurrency())) {
            unsuccesfullDepositingsCounter.increment();
            log.info("Валюта: [{}] не найдена в запросе участника [{}] ", makeDepositingRequest.getReceivedCurrency(), makeDepositingRequest.getParticipantId());
            throw new CurrencyIsNotValidException(makeDepositingRequest.getReceivedCurrency());
        }
        meterRegistry.counter("stockMarket.transactionService.successfulDeposits").increment();
        log.trace("Пользователь ввёл корректную валюту для пополнения: [{}]", makeDepositingRequest.getReceivedCurrency());
        Transaction transaction = new Transaction();
        Participant participant = new Participant();
        participant.setId(makeDepositingRequest.getParticipantId());
        transaction.setOperationType(OperationType.DEPOSITING);
        transaction.setDate(new Date());
        transaction.setParticipant(participant);
        transaction.setReceivedCurrency(makeDepositingRequest.getReceivedCurrency());
        transaction.setReceivedAmount(makeDepositingRequest.getReceivedAmount());
        transaction.setCommission(calculateCommission(transaction.getReceivedAmount(), transaction.getReceivedCurrency()));
        log.info("Внесение средств: [{}]", transaction);
        Transaction saveTransaction = transactionDao.saveTransaction(transaction);
        return saveTransaction;
    }

    public Transaction withdrawal(MakeWithdrawalRequest makeWithdrawalRequest) {
        Counter unsuccesfullWithdrawalsCounter = meterRegistry.counter("webCurrencyService.transactionService.unsuccessfulWithdrawals");
        if (!isParticipantExists(makeWithdrawalRequest.getParticipantId())) {
            unsuccesfullWithdrawalsCounter.increment();
            log.info("Невозможно произвести вывод средств со счёта: участник с id [{}] не найден", makeWithdrawalRequest.getParticipantId());
            throw new ParticipantNotFoundException(makeWithdrawalRequest.getParticipantId());
        }
        if(!webCurrencyService.isValidCurrency(makeWithdrawalRequest.getGivenCurrency())) {
            unsuccesfullWithdrawalsCounter.increment();
            log.info("Валюта: [{}] не найдена в запросе участника [{}]", makeWithdrawalRequest.getGivenCurrency(), makeWithdrawalRequest.getParticipantId());
            throw new CurrencyIsNotValidException(makeWithdrawalRequest.getGivenCurrency());
        }
        if (!isOperationApplicable(makeWithdrawalRequest.getGivenAmount(), makeWithdrawalRequest.getGivenCurrency(), makeWithdrawalRequest.getParticipantId())) {
            unsuccesfullWithdrawalsCounter.increment();
            log.info("Невозможно вывести: [{}] в количестве [{}] у пользователя: [{}] недостаточно средств", makeWithdrawalRequest.getGivenCurrency(), makeWithdrawalRequest.getGivenCurrency(), makeWithdrawalRequest.getParticipantId());
            throw new NotEnoughCurrencyException(makeWithdrawalRequest.getGivenCurrency());
        }
        meterRegistry.counter("webCurrencyService.transactionService.successfulWithdrawals").increment();
        log.trace("Пользователь ввёл корректную валюту для вывода: [{}]", makeWithdrawalRequest.getGivenCurrency());
        log.trace("У пользователя: [{}] хватает средств для проведения операции вывода", makeWithdrawalRequest.getParticipantId());
        Transaction transaction = new Transaction();
        Participant participant = new Participant();
        participant.setId(makeWithdrawalRequest.getParticipantId());
        transaction.setOperationType(OperationType.WITHDRAWAL);
        transaction.setDate(new Date());
        transaction.setParticipant(participant);
        transaction.setGivenCurrency(makeWithdrawalRequest.getGivenCurrency());
        transaction.setGivenAmount(makeWithdrawalRequest.getGivenAmount());
        transaction.setCommission(calculateCommission(makeWithdrawalRequest.getGivenAmount(), makeWithdrawalRequest.getGivenCurrency()));
        log.info("Сохранение транзакции: [{}]", transaction);
        Transaction saveTransaction = transactionDao.saveTransaction(transaction);
        return saveTransaction;
    }

    public Transaction exchange(MakeExchangeRequest makeExchangeRequest) {
        Counter unsuccesfullExchangesCounter = meterRegistry.counter("webCurrencyService.transactionService.unsuccessfulExchanges");
        if (!isParticipantExists(makeExchangeRequest.getParticipantId())) {
            unsuccesfullExchangesCounter.increment();
            log.info("Невозможно произвести операцию обмена: участник с id [{}] не найден", makeExchangeRequest.getParticipantId());
            throw new ParticipantNotFoundException(makeExchangeRequest.getParticipantId());
        }
        String pair = makeExchangeRequest.getGivenCurrency() + makeExchangeRequest.getReceivedCurrency();
        if (!webCurrencyService.isValidCurrencyPair(pair)) {
            unsuccesfullExchangesCounter.increment();
            log.warn("Пользователь [{}] ввёл некорректную пару валют: [{}]", makeExchangeRequest.getParticipantId(), pair);
            throw new CurrencyPairIsNotValidException(pair);
        }
        if (!isOperationApplicable(makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getParticipantId())) {
            unsuccesfullExchangesCounter.increment();
            log.warn("Невозможно обменять [{}] на [{}], в количестве [{}] у пользователя: [{}] недостаточно средств", makeExchangeRequest.getGivenCurrency(), makeExchangeRequest.getReceivedCurrency(), makeExchangeRequest.getGivenAmount(), makeExchangeRequest.getParticipantId());
            throw new NotEnoughCurrencyException(makeExchangeRequest.getGivenCurrency());
        }
        meterRegistry.counter("webCurrencyService.transactionService.successfulExchanges").increment();
        log.trace("Получена валидная пара: [{}]", pair);
        log.trace("У пользователя: [{}] хватает средств для проведения операции вывода", makeExchangeRequest.getParticipantId());
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

        log.info("Сохранение транзакции: [{}]", transaction);
        Transaction saveTransaction = transactionDao.saveTransaction(transaction);
        return saveTransaction;
    }

    public double getBalanceByCurrency(Long participantId, String currency) {
        if(!webCurrencyService.isValidCurrency(currency)) {
            log.info("Пользователь ввёл некорректную валюту: {}", currency);
            throw new CurrencyIsNotValidException(currency);
        }
        if (!isParticipantExists(participantId)) {
            log.info("Невозможно произвести операцию показа баланса по валюте: участник с id [{}] не найден", participantId );
            throw new ParticipantNotFoundException(participantId);
        }
        List<Transaction> transactions = transactionDao.getTransactionsByCurrency(participantId, currency);

        List<Transaction> depositing = new ArrayList<>();
        List<Transaction> replenishment = new ArrayList<>();
        List<Transaction> subtraction = new ArrayList<>();
        List<Transaction> withdrawal = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getOperationType() == OperationType.DEPOSITING) {
                depositing.add(transaction);
            } else if (transaction.getOperationType() == OperationType.EXCHANGE && transaction.getReceivedCurrency().equals(currency)) {
                replenishment.add(transaction);
            } else if (transaction.getOperationType() == OperationType.EXCHANGE && transaction.getGivenCurrency().equals(currency)) {
                subtraction.add(transaction);
            } else if (transaction.getOperationType() == OperationType.WITHDRAWAL) {
                withdrawal.add(transaction);
            }
        }

        double depositingSum = 0;
        double replenishmentSum = 0;
        double subtractionSum = 0;
        double withdrawalSum = 0;

        for (Transaction value : depositing) {
            depositingSum += value.getReceivedAmount() - value.getCommission();
        }
        log.trace("Сумма операций пополения: [{}]", depositingSum);

        for (Transaction value : replenishment) {
            replenishmentSum += value.getReceivedAmount();
        }
        log.trace("Сумма операций по покупке: [{}]", replenishmentSum);

        for (Transaction value : subtraction) {
            subtractionSum += value.getGivenAmount();
        }
        log.trace("Сумма операций по продаже: [{}]", subtractionSum);

        for (Transaction value : withdrawal) {
            withdrawalSum += value.getGivenAmount() - value.getCommission(); // возможно стоит оптимизировать
        }
        log.trace("Сумма операций вывода: [{}]", withdrawalSum);

        double balance = depositingSum + replenishmentSum - withdrawalSum - subtractionSum;
        log.trace("Баланс в валюте [{}] : [{}]", currency, balance);
        return balance;
    }

    public List<Transaction> getTransactionsByFilter(GetTransactionsRequest getTransactionsRequest) {
        if (!isParticipantExists(getTransactionsRequest.getParticipantId())) {
            log.info("Невозможно произвести операцию показа транзакций: участник с id [{}] не найден", getTransactionsRequest.getParticipantId());
            throw new ParticipantNotFoundException(getTransactionsRequest.getParticipantId());
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setParticipantId(getTransactionsRequest.getParticipantId());
        if (getTransactionsRequest.getOperationType() != null) {
            transactionFilter.setOperationType(getTransactionsRequest.getOperationType());
        }
        transactionFilter.setReceivedCurrencies(getTransactionsRequest.getReceivedCurrencies());
        transactionFilter.setReceivedMinAmount(getTransactionsRequest.getReceivedMinAmount());
        transactionFilter.setReceivedMaxAmount(getTransactionsRequest.getReceivedMaxAmount());
        transactionFilter.setGivenCurrencies(getTransactionsRequest.getGivenCurrencies());
        transactionFilter.setGivenMinAmount(getTransactionsRequest.getGivenMinAmount());
        transactionFilter.setGivenMaxAmount(getTransactionsRequest.getGivenMaxAmount());
        transactionFilter.setAfter(getTransactionsRequest.getAfter());
        transactionFilter.setBefore(getTransactionsRequest.getBefore());

        return transactionDao.getTransactionsByFilter(transactionFilter);
    }

    private double calculateCommission(double amount, String currency) {
        double commission = 0;
        double amountOfRub;

        if (!currency.equals(stockMarketSettings.getThresholdBaseCurrency())) {
            amountOfRub = webCurrencyService.convert(currency, amount, stockMarketSettings.getThresholdBaseCurrency());
        } else {
            amountOfRub = amount;
        }

        if (amountOfRub < stockMarketSettings.getThresholdOfCommissionUsage()) {
            commission = amount * stockMarketSettings.getCommissionPercent();
            log.info("Расчёт комисии для валюты: [{}], сумма комисии [{}]", currency, commission);
        } else {
            log.info("Комиссия к данной транзакции не применяется");
        }
        return commission;
    }

    /**
     * Проверка достаточности денежных средств для операции
     *
     * @return
     */
    private boolean isOperationApplicable(double amount, String currency, long participantId) {
        double balance = getBalanceByCurrency(participantId, currency);
        return balance >= amount;
    }

    /**
     * Проверка, что участник с переданным id существует
     *
     * @return
     */
    private boolean isParticipantExists(Long participantId) {
        return participantDao.getParticipantById(participantId) != null;
    }
}
