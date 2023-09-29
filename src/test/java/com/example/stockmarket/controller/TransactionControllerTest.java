package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.*;
import com.example.stockmarket.entity.OperationType;
import com.example.stockmarket.service.transactionService.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TransactionService service;

    private MakeDepositingRequest makeDepositingRequest(long participantId) {
        MakeDepositingRequest makeDepositingRequest = new MakeDepositingRequest();
        makeDepositingRequest.setParticipantId(participantId);
        makeDepositingRequest.setReceivedCurrency("EUR");
        makeDepositingRequest.setReceivedAmount(200.0);
        service.depositing(makeDepositingRequest);
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

        service.depositing(depositing);
        service.exchange(buying);
        service.exchange(selling);
        service.withdrawal(withdrawal);

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
        GetTransactionsRequest depositing = new GetTransactionsRequest();
        depositing.setParticipantId(1L);
        depositing.setOperationType(String.valueOf(OperationType.DEPOSITING));
        depositing.setAfter(new Date(1694034000000L));
        depositing.setBefore(new Date(1694205091000L));
        List<String> receivedCurrencies = new ArrayList<>();
        receivedCurrencies.add("EUR");
        depositing.setReceivedCurrencies(receivedCurrencies);
        depositing.setReceivedMinAmount(49.9);
        depositing.setReceivedMaxAmount(50.1);
        List<String> expectedRoles = new ArrayList<>();
        expectedRoles.add("USER");
        expectedRoles.add("ADMIN");


        mockMvc.perform(get("/transactional/getTransactions")
                        .content(mapper.writeValueAsString(depositing))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].operation_type").value("DEPOSITING"))
                .andExpect(jsonPath("[0].date").value(1694034000000L))
                .andExpect(jsonPath("[0].received_currency").value("EUR"))
                .andExpect(jsonPath("[0].received_amount").value(50))
                .andExpect(jsonPath("[0].given_amount").value(0.0))
                .andExpect(jsonPath("[0].commission").value(2.5))
                .andExpect(jsonPath("[0].participant.id").value(1))
                .andExpect(jsonPath("[0].participant.name").value("Pavel"))
                .andExpect(jsonPath("[0].participant.password").value("pasw123"))
                .andExpect(jsonPath("[0].participant.roles.length()").value(expectedRoles.size()))
//                .andExpect(jsonPath("[0].participant.roles.[0]").value("USER"))
//                .andExpect(jsonPath("[0].participant.roles.[1]").value("ADMIN"))
                .andExpect(jsonPath("[0].participant.enabled").value(true))
                .andExpect(jsonPath("[0].participant.creation_date").value(1694206800000L))
                .andDo(print());
    }

    @Test
    void getTransactionsByFilterTest1() throws Exception {
        GetTransactionsRequest exchange = new GetTransactionsRequest();
        exchange.setParticipantId(1L);
        exchange.setOperationType(String.valueOf(OperationType.EXCHANGE));
        exchange.setAfter(new Date(1694034000000L));
        exchange.setBefore(new Date(1694205091000L));
        List<String> receivedCurrencies = new ArrayList<>();
        receivedCurrencies.add("EUR");
        List<String> givenCurrencies = new ArrayList<>();
        givenCurrencies.add("RUB");
        exchange.setReceivedCurrencies(receivedCurrencies);
        exchange.setGivenCurrencies(givenCurrencies);
        exchange.setReceivedMaxAmount(20.58);
        exchange.setGivenMaxAmount(1500.01);
        List<String> expectedRoles = new ArrayList<>();
        expectedRoles.add("USER");
        expectedRoles.add("ADMIN");


        mockMvc.perform(get("/transactional/getTransactions")
                        .content(mapper.writeValueAsString(exchange))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(2))
                .andExpect(jsonPath("[0].operation_type").value("EXCHANGE"))
                .andExpect(jsonPath("[0].date").value(1694034000000L))
                .andExpect(jsonPath("[0].received_currency").value("EUR"))
                .andExpect(jsonPath("[0].received_amount").value(20.58))
                .andExpect(jsonPath("[0].given_currency").value("RUB"))
                .andExpect(jsonPath("[0].given_amount").value(1500.0))
                .andExpect(jsonPath("[0].commission").value(75.0))
                .andExpect(jsonPath("[0].participant.id").value(1))
                .andExpect(jsonPath("[0].participant.name").value("Pavel"))
                .andExpect(jsonPath("[0].participant.password").value("pasw123"))
                .andExpect(jsonPath("[0].participant.roles.length()").value(expectedRoles.size()))
//                .andExpect(jsonPath("[0].participant.roles.[0]").value("USER"))
//                .andExpect(jsonPath("[0].participant.roles.[1]").value("ADMIN"))
                .andExpect(jsonPath("[0].participant.enabled").value(true))
                .andExpect(jsonPath("[0].participant.creation_date").value(1694206800000L))
                .andDo(print());
    }
    @Test
    void getTransactionsByFilterTest2() throws Exception {
        GetTransactionsRequest withdrawal = new GetTransactionsRequest();
        withdrawal.setParticipantId(1L);
        withdrawal.setOperationType(String.valueOf(OperationType.WITHDRAWAL));
        withdrawal.setAfter(new Date(1694034000000L));
        withdrawal.setBefore(new Date(1694034000001L));
        List<String> givenCurrencies = new ArrayList<>();
        givenCurrencies.add("EUR");
        withdrawal.setGivenCurrencies(givenCurrencies);
        withdrawal.setGivenMaxAmount(10.0);
        List<String> expectedRoles = new ArrayList<>();
        expectedRoles.add("USER");
        expectedRoles.add("ADMIN");


        mockMvc.perform(get("/transactional/getTransactions")
                        .content(mapper.writeValueAsString(withdrawal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(4))
                .andExpect(jsonPath("[0].operation_type").value("WITHDRAWAL"))
                .andExpect(jsonPath("[0].date").value(1694034000000L))
                .andExpect(jsonPath("[0].received_amount").value(0.0))
                .andExpect(jsonPath("[0].given_currency").value("EUR"))
                .andExpect(jsonPath("[0].given_amount").value(5))
                .andExpect(jsonPath("[0].commission").value(0.25))
                .andExpect(jsonPath("[0].participant.id").value(1))
                .andExpect(jsonPath("[0].participant.name").value("Pavel"))
                .andExpect(jsonPath("[0].participant.password").value("pasw123"))
                .andExpect(jsonPath("[0].participant.roles.length()").value(expectedRoles.size()))
//                .andExpect(jsonPath("[0].participant.roles.[0]").value("USER"))
//                .andExpect(jsonPath("[0].participant.roles.[1]").value("ADMIN"))
                .andExpect(jsonPath("[0].participant.enabled").value(true))
                .andExpect(jsonPath("[0].participant.creation_date").value(1694206800000L))
                .andDo(print());
    }
}
