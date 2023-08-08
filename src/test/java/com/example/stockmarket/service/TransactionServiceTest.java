package com.example.stockmarket.service;

import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.InputOutputRequest;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.transactionService.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private TransactionService service;

    private InputOutputRequest createTransactionRequest(long participantId) {
        InputOutputRequest inputOutputRequest = new InputOutputRequest();
        inputOutputRequest.setParticipantId(participantId);
        inputOutputRequest.setGivenCurrency("EUR");
        inputOutputRequest.setGivenAmount(200.0);
        service.depositing(inputOutputRequest);
        return inputOutputRequest;
    }

    @Test
    public void depositingTest() {
        InputOutputRequest request = new InputOutputRequest();
        request.setParticipantId(1L);
        request.setGivenCurrency("EUR");
        request.setGivenAmount(50.0);

       Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.DEPOSITING);
        expectedTransaction.setDate(new Date());
        expectedTransaction.setId(1L);
        expectedTransaction.setGivenAmount(50.0);
        expectedTransaction.setParticipantId(1L);
        expectedTransaction.setGivenCurrency("EUR");
        expectedTransaction.setCommission(2.5);
        Transaction actualTransaction = service.depositing(request);
        expectedTransaction.setId(actualTransaction.getId());
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void withdrawalTest() {
        createTransactionRequest(1L);
        InputOutputRequest request = new InputOutputRequest();
        request.setParticipantId(1L);
        request.setGivenCurrency("EUR");
        request.setGivenAmount(50.0);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.WITHDRAWAL);
        expectedTransaction.setId(1L);
        expectedTransaction.setGivenAmount(50.0);
        expectedTransaction.setParticipantId(1L);
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
        request.setParticipantId(1L);
        request.setGivenCurrency("EUR");
        request.setRequiredCurrency("RUB");
        request.setGivenAmount(20.0);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.EXCHANGE);
        expectedTransaction.setId(1L);
        expectedTransaction.setGivenAmount(20.0);
        expectedTransaction.setReceivedAmount(1315.636);
        expectedTransaction.setParticipantId(1L);
        expectedTransaction.setGivenCurrency("EUR");
        expectedTransaction.setReceivedCurrency("RUB");
        expectedTransaction.setCommission(1);
        Transaction actualTransaction = service.exchange(request);
        expectedTransaction.setId(actualTransaction.getId());
        expectedTransaction.setDate(actualTransaction.getDate());
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    public void getBalanceByCurrency() {
//        Double expectedResult = 44.33;
        double expectedResult = 195.8322725;
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setParticipantId(1L);
        transactionRequest.setGivenCurrency("EUR");

        createTransactionRequest(1L);

        InputOutputRequest depositing = new InputOutputRequest();
        depositing.setParticipantId(1L);
        depositing.setGivenCurrency("RUB");
        depositing.setGivenAmount(2000.0);

        MakeExchangeRequest buying = new MakeExchangeRequest();
        buying.setParticipantId(1L);
        buying.setGivenCurrency("RUB");
        buying.setRequiredCurrency("EUR");
        buying.setGivenAmount(1500.0);

        MakeExchangeRequest selling = new MakeExchangeRequest();
        selling.setParticipantId(1L);
        selling.setGivenCurrency("EUR");
        selling.setRequiredCurrency("RUB");
        selling.setGivenAmount(20.0);

        InputOutputRequest withdrawal = new InputOutputRequest();
        withdrawal.setParticipantId(1L);
        withdrawal.setGivenCurrency("EUR");
        withdrawal.setGivenAmount(5.0);

        service.depositing(depositing);
        service.exchange(buying);
        service.exchange(selling);
        service.withdrawal(withdrawal);

        Assertions.assertEquals(expectedResult, service.getBalanceByCurrency(transactionRequest.getParticipantId(), transactionRequest.getGivenCurrency()));
    }
}
