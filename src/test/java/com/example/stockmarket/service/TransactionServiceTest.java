package com.example.stockmarket.service;

import com.example.stockmarket.controller.request.transactionRequest.*;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.transactionService.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class TransactionServiceTest extends TestWithWebCurrency {
    @Autowired
    private TransactionService service;

    private MakeDepositingRequest createTransactionRequest(long participantId) {
        setExpectedWebCurrencyServiceResponseForAvailableCurrencies();
        setExpectedWebCurrencyServiceResponseForCurrencyRate("EURRUB", "69.244");
        MakeDepositingRequest makeDepositingRequest = new MakeDepositingRequest();
        makeDepositingRequest.setParticipantId(participantId);
        makeDepositingRequest.setReceivedCurrency("EUR");
        makeDepositingRequest.setReceivedAmount(200.0);
        service.depositing(makeDepositingRequest);
        return makeDepositingRequest;
    }

    @Test
    public void depositingTest() {
        MakeDepositingRequest request = new MakeDepositingRequest();

        Participant participant = new Participant();
        participant.setId(1L);
        request.setParticipantId(1L);
        request.setReceivedCurrency("EUR");
        request.setReceivedAmount(50.0);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.DEPOSITING);
        expectedTransaction.setDate(new Date());
        expectedTransaction.setId(1L);
        expectedTransaction.setReceivedAmount(50.0);
        expectedTransaction.setParticipant(participant);
        expectedTransaction.setReceivedCurrency("EUR");
        expectedTransaction.setCommission(2.5);

        setExpectedWebCurrencyServiceResponseForAvailableCurrencies();
        setExpectedWebCurrencyServiceResponseForCurrencyRate("EURRUB", "69.244");
        Transaction actualTransaction = service.depositing(request);

        expectedTransaction.setId(actualTransaction.getId());
        expectedTransaction.setDate(actualTransaction.getDate());
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void withdrawalTest() {
        createTransactionRequest(1L);
        MakeWithdrawalRequest request = new MakeWithdrawalRequest();
        Participant participant = new Participant();
        participant.setId(1L);
        request.setParticipantId(1L);
        request.setGivenCurrency("EUR");
        request.setGivenAmount(50.0);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.WITHDRAWAL);
        expectedTransaction.setId(1L);
        expectedTransaction.setGivenAmount(50.0);
        expectedTransaction.setParticipant(participant);
        expectedTransaction.setGivenCurrency("EUR");
        expectedTransaction.setCommission(2.5);
        Transaction actualTransaction = service.withdrawal(request);
        expectedTransaction.setId(actualTransaction.getId());
        expectedTransaction.setDate(actualTransaction.getDate());
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void exchange() {
        createTransactionRequest(1L);
        MakeExchangeRequest request = new MakeExchangeRequest();
        Participant participant = new Participant();
        participant.setId(1L);
        request.setParticipantId(1L);
        request.setGivenCurrency("EUR");
        request.setReceivedCurrency("RUB");
        request.setGivenAmount(20.0);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.EXCHANGE);
        expectedTransaction.setId(1L);
        expectedTransaction.setGivenAmount(20.0);
        expectedTransaction.setReceivedAmount(1315.636);
        expectedTransaction.setParticipant(participant);
        expectedTransaction.setGivenCurrency("EUR");
        expectedTransaction.setReceivedCurrency("RUB");
        expectedTransaction.setCommission(1.0);
        Transaction actualTransaction = service.exchange(request);
        expectedTransaction.setId(actualTransaction.getId());
        expectedTransaction.setDate(actualTransaction.getDate());
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void getBalanceByCurrency() {
        double expectedResult = 195.8322725;
        GetBalanceRequest getBalanceRequest = new GetBalanceRequest();
        getBalanceRequest.setParticipantId(2L);
        getBalanceRequest.setCurrency("EUR");

        createTransactionRequest(2L);

        MakeDepositingRequest depositing = new MakeDepositingRequest();
        depositing.setParticipantId(2L);
        depositing.setReceivedCurrency("RUB");
        depositing.setReceivedAmount(2000.0);

        MakeExchangeRequest buying = new MakeExchangeRequest();
        buying.setParticipantId(2L);
        buying.setGivenCurrency("RUB");
        buying.setReceivedCurrency("EUR");
        buying.setGivenAmount(1500.0);

        MakeExchangeRequest selling = new MakeExchangeRequest();
        selling.setParticipantId(2L);
        selling.setGivenCurrency("EUR");
        selling.setReceivedCurrency("RUB");
        selling.setGivenAmount(20.0);

        MakeWithdrawalRequest withdrawal = new MakeWithdrawalRequest();
        withdrawal.setParticipantId(2L);
        withdrawal.setGivenCurrency("EUR");
        withdrawal.setGivenAmount(5.0);

        service.depositing(depositing);
        service.exchange(selling);

        setExpectedWebCurrencyServiceResponseForCurrencyRate("RUBEUR", "0.0144437");

        service.exchange(buying);
        service.withdrawal(withdrawal);

        Assertions.assertEquals(expectedResult, service.getBalanceByCurrency(getBalanceRequest.getParticipantId(), getBalanceRequest.getCurrency()));
    }
}