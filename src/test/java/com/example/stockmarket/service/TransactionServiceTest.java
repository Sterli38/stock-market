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
    private Transaction transaction;

    @BeforeEach
    void setup() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setDate(new Date(1690290287));
        transaction.setParticipantId(1L);
        transaction.setReceivedCurrency("EUR");
        transaction.setAmount(1000);
        transaction.setCommission(0.0);
        transaction.setId(service.depositing(transaction).getId() + 1);

    }

    @Test
    public void depositingTest() {
        transaction.setOperationType(OperationType.DEPOSITING);

        Transaction testTransaction = new Transaction();
        testTransaction.setDate(new Date(1690290287));
        testTransaction.setId(1L);
        testTransaction.setAmount(1000);
        testTransaction.setParticipantId(1L);
        testTransaction.setReceivedCurrency("EUR");
        Assertions.assertEquals(transaction, service.depositing(testTransaction));
    }

    @Test
    public void buyTest_isValid() {
        transaction.setOperationType(OperationType.BUYING);
        transaction.setGivenCurrency("RUB");
        transaction.setAmount(14.4437);
        transaction.setCommission(0.7221850000000001);

        Transaction testTransaction = new Transaction();
        testTransaction.setDate(new Date(1690290287));
        testTransaction.setId(1L);
        testTransaction.setAmount(1000);
        testTransaction.setParticipantId(1L);
        testTransaction.setReceivedCurrency("EUR");
        testTransaction.setGivenCurrency("RUB");
        Assertions.assertEquals(transaction, service.buy(testTransaction));
    }

    @Test
    public void sellTest() {
        transaction.setOperationType(OperationType.SELLING);
        transaction.setGivenCurrency("RUB");
        transaction.setCommission(50);

        Transaction testTransaction = new Transaction();
        testTransaction.setDate(new Date(1690290287));
        testTransaction.setId(1L);
        testTransaction.setAmount(1000);
        testTransaction.setParticipantId(1L);
        testTransaction.setReceivedCurrency("EUR");
        testTransaction.setGivenCurrency("RUB");
        Assertions.assertEquals(transaction, service.sell(testTransaction)); // не работает
    }

    @Test
    public void getBalanceByCurrency() {
//        double expectedResult = 63.721514999999954;
        double expectedResult = 1063.721515;
        Transaction depositing = new Transaction();
        depositing.setOperationType(OperationType.DEPOSITING);
        depositing.setDate(new Date(1690290287));
        depositing.setAmount(1000);
        depositing.setParticipantId(1L);
        depositing.setReceivedCurrency("EUR");

        Transaction buying = new Transaction();
        depositing.setOperationType(OperationType.BUYING);
        buying.setDate(new Date(1690290287));
        buying.setAmount(1000);
        buying.setParticipantId(1L);
        buying.setReceivedCurrency("EUR");
        buying.setGivenCurrency("RUB");

        Transaction selling = new Transaction();
        depositing.setOperationType(OperationType.SELLING);
        selling.setDate(new Date(1690290287));
        selling.setAmount(1000);
        selling.setParticipantId(1L);
        selling.setReceivedCurrency("EUR");
        selling.setGivenCurrency("RUB");

        service.depositing(depositing);
        service.buy(buying);
        service.sell(selling);

        Transaction transactionRequest = new Transaction();
        transactionRequest.setParticipantId(1L);
        transactionRequest.setReceivedCurrency("EUR");


        Assertions.assertEquals(expectedResult, service.getBalanceByCurrency(transactionRequest));
    }
}
