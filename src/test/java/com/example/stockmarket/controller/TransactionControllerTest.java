package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.*;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.participantService.ParticipantService;
import com.example.stockmarket.service.transactionService.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username="admin",authorities={"ADMIN"})
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ParticipantService participantService;

    private MakeDepositingRequest makeDepositingRequest(long participantId) {
        MakeDepositingRequest makeDepositingRequest = new MakeDepositingRequest();
        makeDepositingRequest.setParticipantId(participantId);
        makeDepositingRequest.setReceivedCurrency("EUR");
        makeDepositingRequest.setReceivedAmount(200.0);
        transactionService.depositing(makeDepositingRequest);
        return makeDepositingRequest;
    }

    @Test
    void makeDepositingTest() throws Exception {
        MakeDepositingRequest testRequest = makeDepositingRequest(1);

        mockMvc.perform(post("/transactional/makeDepositing")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber());
    }

    @Test
    void makeWithdrawalTest() throws Exception {
        makeDepositingRequest(1);

        MakeWithdrawalRequest testRequest = new MakeWithdrawalRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setGivenAmount(50.0);

        mockMvc.perform(get("/transactional/withdrawal")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber());
    }

    @Test
    void makeBadWithdrawalTest() throws Exception {
        makeDepositingRequest(1);

        MakeWithdrawalRequest testRequest = new MakeWithdrawalRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setGivenAmount(5000.0);

        mockMvc.perform(get("/transactional/withdrawal")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("not enough currency in : EUR"));
    }

    @Test
    void exchangeTest() throws Exception {
        makeDepositingRequest(1L);

        MakeExchangeRequest testRequest = new MakeExchangeRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setReceivedCurrency("RUB");
        testRequest.setGivenAmount(50.0);

        mockMvc.perform(post("/transactional/exchange")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber());
    }

    @Test
    void badExchangeTest() throws Exception {
        makeDepositingRequest(1L);

        MakeExchangeRequest testRequest = new MakeExchangeRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setReceivedCurrency("RUB");
        testRequest.setGivenAmount(50000.0);

        mockMvc.perform(post("/transactional/exchange")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("not enough currency in : EUR"));
    }

    @Test
    void getBalanceByCurrencyTest() throws Exception {

        GetBalanceRequest getBalanceRequest = new GetBalanceRequest();
        getBalanceRequest.setParticipantId(2L);
        getBalanceRequest.setCurrency("EUR");

        makeDepositingRequest(2);

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

        transactionService.depositing(depositing);
        transactionService.exchange(buying);
        transactionService.exchange(selling);
        transactionService.withdrawal(withdrawal);

        mockMvc.perform(get("/transactional/getBalanceByCurrency")
                        .content(mapper.writeValueAsString(getBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("currency_balance").value(195.8322725));
    }

    @Test
    void getBadBalanceByCurrencyTest() throws Exception {

        GetBalanceRequest getBalanceRequest = new GetBalanceRequest();
        getBalanceRequest.setParticipantId(2L);
        getBalanceRequest.setCurrency("EURO"); // Проверка на валюту на которой нет транзакций

        mockMvc.perform(get("/transactional/getBalanceByCurrency")
                        .content(mapper.writeValueAsString(getBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message").value("Currency EURO is not valid"));
    }

    @Test
    void TestForReceivingAllTransactionsInOneCurrency() throws Exception {
        Participant participant = new Participant();
        participant.setName("ParticipantForGetTransactionsByTest");
        participant.setPassword("TestPassword");
        participant.setEnabled(true);
        Role roleUser = new Role();
        roleUser.setRoleName("USER");
        Role roleReader = new Role();
        roleReader.setRoleName("READER");
        Set<Role> participantRoles = new HashSet<>();
        participantRoles.add(roleReader);
        participantRoles.add(roleUser);
        participant.setRoles(participantRoles);
        participant.setCreationDate(new Date(1696232828000L));
        Long participantId = participantService.createParticipant(participant).getId();

        MakeDepositingRequest depositingRequest = new MakeDepositingRequest();
        depositingRequest.setParticipantId(participantId);
        depositingRequest.setReceivedCurrency("EUR");
        depositingRequest.setReceivedAmount(20.0);
        Transaction depositing = transactionService.depositing(depositingRequest);

        MakeExchangeRequest exchangeRequest = new MakeExchangeRequest();
        exchangeRequest.setGivenCurrency("EUR");
        exchangeRequest.setGivenAmount(5.0);
        exchangeRequest.setReceivedCurrency("RUB");
        exchangeRequest.setParticipantId(participantId);
        Transaction exchange = transactionService.exchange(exchangeRequest);

        MakeWithdrawalRequest withdrawalRequest = new MakeWithdrawalRequest();
        withdrawalRequest.setParticipantId(participantId);
        withdrawalRequest.setGivenCurrency("EUR");
        withdrawalRequest.setGivenAmount(10.0);
        Transaction withdrawal = transactionService.withdrawal(withdrawalRequest);

        GetTransactionsRequest getTransactionsRequest = new GetTransactionsRequest();
        getTransactionsRequest.setParticipantId(participantId);
        List<String> receivedCurrencies = new ArrayList<>();
        receivedCurrencies.add("EUR");
        getTransactionsRequest.setReceivedCurrencies(receivedCurrencies);
        List<String> givenCurrencies = new ArrayList<>();
        givenCurrencies.add("EUR");
        getTransactionsRequest.setGivenCurrencies(givenCurrencies);

        mockMvc.perform(get("/transactional/getTransactions")
                .content(mapper.writeValueAsString(getTransactionsRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(depositing.getId()))
                .andExpect(jsonPath("[0].operation_type").value(OperationType.DEPOSITING.toString()))
                .andExpect(jsonPath("[0].date").value(depositing.getDate()))
                .andExpect(jsonPath("[0].received_currency").value("EUR"))
                .andExpect(jsonPath("[0].received_amount").value(20))
                .andExpect(jsonPath("[0].given_amount").value(0.0))
                .andExpect(jsonPath("[0].participant.id").value(participantId))
                .andExpect(jsonPath("[0].participant.name").value("ParticipantForGetTransactionsByTest"))
                .andExpect(jsonPath("[0].participant.password").value("TestPassword"))
//                .andExpect(jsonPath("[0].participant.roles[0]").value("READER"))
//                .andExpect(jsonPath("[0].participant.roles[1]").value("USER"))
                .andExpect(jsonPath("[0].participant.enabled").value(true))
                .andExpect(jsonPath("[0].participant.creation_date").value(1696232828000L))

                .andExpect(jsonPath("[1].id").value(exchange.getId()))
                .andExpect(jsonPath("[1].operation_type").value(OperationType.EXCHANGE.toString()))
                .andExpect(jsonPath("[1].date").value(exchange.getDate()))
                .andExpect(jsonPath("[1].received_currency").value("RUB"))
                .andExpect(jsonPath("[1].given_currency").value("EUR"))
                .andExpect(jsonPath("[1].received_amount").value(exchange.getReceivedAmount()))
                .andExpect(jsonPath("[1].given_amount").value(5.0))
                .andExpect(jsonPath("[1].participant.id").value(participantId))
                .andExpect(jsonPath("[1].participant.name").value("ParticipantForGetTransactionsByTest"))
                .andExpect(jsonPath("[1].participant.password").value("TestPassword"))
//                .andExpect(jsonPath("[1].participant.roles[0]").value("READER"))
//                .andExpect(jsonPath("[1].participant.roles[1]").value("USER"))
                .andExpect(jsonPath("[1].participant.enabled").value(true))
                .andExpect(jsonPath("[1].participant.creation_date").value(1696232828000L))

                .andExpect(jsonPath("[2].id").value(withdrawal.getId()))
                .andExpect(jsonPath("[2].operation_type").value(OperationType.WITHDRAWAL.toString()))
                .andExpect(jsonPath("[2].date").value(withdrawal.getDate()))
                .andExpect(jsonPath("[2].received_amount").value(0.0))
                .andExpect(jsonPath("[2].given_currency").value("EUR"))
                .andExpect(jsonPath("[2].given_amount").value(10.0))
                .andExpect(jsonPath("[2].participant.id").value(participantId))
                .andExpect(jsonPath("[2].participant.name").value("ParticipantForGetTransactionsByTest"))
                .andExpect(jsonPath("[2].participant.password").value("TestPassword"))
//                .andExpect(jsonPath("[2].participant.roles[0]").value("READER"))
//                .andExpect(jsonPath("[2].participant.roles[1]").value("USER"))
                .andExpect(jsonPath("[2].participant.enabled").value(true))
                .andExpect(jsonPath("[2].participant.creation_date").value(1696232828000L))
                .andDo(print());
    }

    @Test
    void TestForReceivingExchangeTransaction() throws Exception {
        Participant participant = new Participant();
        participant.setName("ParticipantForGetTransactionsByTest2");
        participant.setPassword("TestPassword2");
        participant.setEnabled(true);
        Role roleUser = new Role();
        roleUser.setRoleName("USER");
        Set<Role> participantRoles = new HashSet<>();
        participantRoles.add(roleUser);
        participant.setRoles(participantRoles);
        participant.setCreationDate(new Date(1696232828000L));
        Long participantId = participantService.createParticipant(participant).getId();

        MakeDepositingRequest depositing = new MakeDepositingRequest();
        depositing.setParticipantId(participantId);
        depositing.setReceivedCurrency("RUB");
        depositing.setReceivedAmount(5000.0);
        transactionService.depositing(depositing);

        MakeExchangeRequest exchangeRequest = new MakeExchangeRequest();
        exchangeRequest.setGivenCurrency("RUB");
        exchangeRequest.setGivenAmount(5000.0);
        exchangeRequest.setReceivedCurrency("EUR");
        exchangeRequest.setParticipantId(participantId);
        Transaction exchange = transactionService.exchange(exchangeRequest);

        GetTransactionsRequest getTransactionsRequest = new GetTransactionsRequest();
        getTransactionsRequest.setParticipantId(participantId);
        getTransactionsRequest.setOperationType(OperationType.EXCHANGE);

        mockMvc.perform(get("/transactional/getTransactions")
                        .content(mapper.writeValueAsString(getTransactionsRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(exchange.getId()))
                .andExpect(jsonPath("[0].operation_type").value(OperationType.EXCHANGE.toString()))
                .andExpect(jsonPath("[0].date").value(exchange.getDate()))
                .andExpect(jsonPath("[0].given_currency").value("RUB"))
                .andExpect(jsonPath("[0].received_amount").value(exchange.getReceivedAmount()))
                .andExpect(jsonPath("[0].given_amount").value(5000.0))
                .andExpect(jsonPath("[0].received_currency").value("EUR"))
                .andExpect(jsonPath("[0].participant.id").value(participantId))
                .andExpect(jsonPath("[0].participant.name").value("ParticipantForGetTransactionsByTest2"))
                .andExpect(jsonPath("[0].participant.password").value("TestPassword2"))
//                .andExpect(jsonPath("[0].participant.roles[0]").value("USER"))
                .andExpect(jsonPath("[0].participant.enabled").value(true))
                .andExpect(jsonPath("[0].participant.creation_date").value(1696232828000L))
                .andDo(print());
    }

    @Test
    void TestForReceivingTransactionsByDate() throws Exception {
        Participant participant = new Participant();
        participant.setName("ParticipantForGetTransactionsByTest3");
        participant.setPassword("TestPassword3");
        participant.setEnabled(true);
        Role roleUser = new Role();
        roleUser.setRoleName("USER");
        Set<Role> participantRoles = new HashSet<>();
        participantRoles.add(roleUser);
        participant.setRoles(participantRoles);
        participant.setCreationDate(new Date(1696232828000L));
        Long participantId = participantService.createParticipant(participant).getId();

        MakeDepositingRequest depositingRequest = new MakeDepositingRequest();
        depositingRequest.setParticipantId(participantId);
        depositingRequest.setReceivedCurrency("RUB");
        depositingRequest.setReceivedAmount(5000.0);
        Transaction expectedTransaction = transactionService.depositing(depositingRequest);

        GetTransactionsRequest getTransactionsRequest = new GetTransactionsRequest();
        getTransactionsRequest.setParticipantId(participantId);
        getTransactionsRequest.setAfter(new Date(1633779212000L));

        mockMvc.perform(get("/transactional/getTransactions")
                        .content(mapper.writeValueAsString(getTransactionsRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(expectedTransaction.getId()))
                .andExpect(jsonPath("[0].operation_type").value(OperationType.DEPOSITING.toString()))
                .andExpect(jsonPath("[0].date").value(expectedTransaction.getDate()))
                .andExpect(jsonPath("[0].received_amount").value(5000.0))
                .andExpect(jsonPath("[0].given_amount").value(0.0))
                .andExpect(jsonPath("[0].received_currency").value("RUB"))
                .andExpect(jsonPath("[0].participant.id").value(participantId))
                .andExpect(jsonPath("[0].participant.name").value("ParticipantForGetTransactionsByTest3"))
                .andExpect(jsonPath("[0].participant.password").value("TestPassword3"))
//                .andExpect(jsonPath("[0].participant.roles[0]").value("USER"))
                .andExpect(jsonPath("[0].participant.enabled").value(true))
                .andExpect(jsonPath("[0].participant.creation_date").value(1696232828000L))
                .andDo(print());
    }
}
