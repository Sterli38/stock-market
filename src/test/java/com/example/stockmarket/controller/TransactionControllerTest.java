package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.*;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.entity.Role;
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
                .andExpect(jsonPath("currency_balance").value(0));
    }

    @Test
    void getTransactionsByFilterTest() throws Exception {
        Participant participant = new Participant();
        participant.setName("ParticipantForGetTransactionsByTest");
        participant.setPassword("TestPassword");
        participant.setEnabled(true);
        Set<Role> participantRoles = new HashSet<>();
        participantRoles.add(Role.READER);
        participantRoles.add(Role.USER);
        participant.setRoles(participantRoles);
        participant.setCreationDate(new Date(1696232828000L));
        Long participantId = participantService.createParticipant(participant).getId();

        MakeDepositingRequest depositing = new MakeDepositingRequest();
        depositing.setParticipantId(participantId);
        depositing.setReceivedCurrency("EUR");
        depositing.setReceivedAmount(20.0);
        transactionService.depositing(depositing);

        MakeExchangeRequest exchange = new MakeExchangeRequest();
        exchange.setGivenCurrency("EUR");
        exchange.setGivenAmount(5.0);
        exchange.setReceivedCurrency("RUB");
        exchange.setParticipantId(participantId);
        transactionService.exchange(exchange);

        MakeWithdrawalRequest withdrawalRequest = new MakeWithdrawalRequest();
        withdrawalRequest.setParticipantId(participantId);
        withdrawalRequest.setGivenCurrency("EUR");
        withdrawalRequest.setGivenAmount(10.0);
        transactionService.withdrawal(withdrawalRequest);

        GetTransactionsRequest getTransactionsRequest = new GetTransactionsRequest();
        getTransactionsRequest.setParticipantId(participantId);
        List<String> receivedCurrencies = new ArrayList<>();
        receivedCurrencies.add("EUR");
        getTransactionsRequest.setReceivedCurrencies(receivedCurrencies);
//        List<String> givenCurrencies = new ArrayList<>();
//        givenCurrencies.add("EUR");
//        getTransactionsRequest.setGivenCurrencies(givenCurrencies);

        mockMvc.perform(get("/transactional/getTransactions")
                .content(mapper.writeValueAsString(getTransactionsRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].participant.name").value("ParticipantForGetTransactionsByTest"))
                .andDo(print());

    }
}
