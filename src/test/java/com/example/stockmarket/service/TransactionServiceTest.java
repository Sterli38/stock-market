package com.example.stockmarket.service;

import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.transactionService.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private TransactionService service;

    private Transaction createExpectedTransaction() {
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setOperationType(OperationType.DEPOSITING);
        expectedTransaction.setDate(new Date(1690290287));
        expectedTransaction.setAmount(1000);
        expectedTransaction.setParticipantId(1L);
        expectedTransaction.setReceivedCurrency("EUR");
        expectedTransaction.setAmount(1000);
        expectedTransaction.setCommission(0.0);
        expectedTransaction.setId(service.depositing(expectedTransaction).getId() + 1); /////////
        return expectedTransaction;
    }

    @Test
    public void depositingTest() {
        Transaction expectedTransaction = createExpectedTransaction();

        Transaction testTransaction = new Transaction();
        testTransaction.setOperationType(OperationType.DEPOSITING);
        testTransaction.setDate(new Date(1690290287));
        testTransaction.setId(1L);
        testTransaction.setAmount(1000);
        testTransaction.setParticipantId(1L);
        testTransaction.setReceivedCurrency("EUR");
        Assertions.assertEquals(expectedTransaction, service.depositing(testTransaction));
    }

    @Test
    public void withdrawalTest() {
        Transaction expectedTransaction = createExpectedTransaction();
        expectedTransaction.setOperationType(OperationType.WITHDRAWAL);
        expectedTransaction.setReceivedCurrency(null);
        expectedTransaction.setGivenCurrency("EUR");

        Transaction testTransaction = new Transaction();
        testTransaction.setDate(new Date(1690290287));
        testTransaction.setId(1L);
        testTransaction.setAmount(1000);
        testTransaction.setParticipantId(1L);
        testTransaction.setGivenCurrency("EUR");
        testTransaction.setOperationType(OperationType.WITHDRAWAL);
        Assertions.assertEquals(expectedTransaction, service.withdrawal(testTransaction));
    }

    @Test
    public void exchange() {

    }

    @Test
    public void getBalanceByCurrency() {
        double expectedResult = 0;
        Transaction depositing = new Transaction();
        depositing.setOperationType(OperationType.DEPOSITING);
        depositing.setDate(new Date(1690290287));
        depositing.setAmount(10);
        depositing.setParticipantId(1L);
        depositing.setReceivedCurrency("EUR");

        Transaction buying = new Transaction();
        depositing.setOperationType(OperationType.EXCHANGE);
        buying.setDate(new Date(1690290287));
        buying.setAmount(1000);
        buying.setParticipantId(1L);
        buying.setReceivedCurrency("EUR");
        buying.setGivenCurrency("RUB");

        Transaction selling = new Transaction();
        depositing.setOperationType(OperationType.EXCHANGE);
        selling.setDate(new Date(1690290287));
        selling.setAmount(40);
        selling.setParticipantId(1L);
        selling.setReceivedCurrency("RUB");
        selling.setGivenCurrency("EUR");

        Transaction withdrawal = new Transaction();
        depositing.setOperationType(OperationType.WITHDRAWAL);
        withdrawal.setDate(new Date(1690290287));
        withdrawal.setAmount(10);
        withdrawal.setParticipantId(1L);
        withdrawal.setGivenCurrency("EUR");

        service.depositing(depositing);
        service.exchange(buying);
        service.exchange(selling);
        service.withdrawal(withdrawal);

        Transaction transactionRequest = new Transaction();
        transactionRequest.setParticipantId(1L);
        transactionRequest.setReceivedCurrency("EUR");

        Assertions.assertEquals(expectedResult, service.getBalanceByCurrency(transactionRequest.getId(), transactionRequest.getReceivedCurrency()));
    }
}
