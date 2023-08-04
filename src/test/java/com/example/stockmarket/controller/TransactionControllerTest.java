package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.BalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.service.transactionService.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TransactionService service;

    private TransactionRequest makeDepositingRequest(long participantId) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setParticipantId(participantId);
        transactionRequest.setGivenCurrency("EUR");
        transactionRequest.setGivenAmount(200.0);
        service.depositing(transactionRequest);
        return transactionRequest;
    }

    @Test
    void makeDepositingTest() throws Exception {
        TransactionRequest testRequest = makeDepositingRequest(1);

        mockMvc.perform(post("/transactional/makeDepositing")
                .content(mapper.writeValueAsString(testRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId").isNumber());
    }

    @Test
    void makeWithdrawalTest() throws Exception {
        makeDepositingRequest(1);

        TransactionRequest testRequest = new TransactionRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setGivenAmount(50.0);

        mockMvc.perform(get("/transactional/withdrawal")
                .content(mapper.writeValueAsString(testRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId").isNumber());
    }

    @Test
    void makeBadWithdrawalTest() throws Exception {
        makeDepositingRequest(1);

        TransactionRequest testRequest = new TransactionRequest();
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
        testRequest.setRequiredCurrency("RUB");
        testRequest.setGivenAmount(50.0);

        mockMvc.perform(post("/transactional/exchange")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId").isNumber());
    }

    @Test
    void badExchangeTest() throws Exception {
        makeDepositingRequest(1L);

        MakeExchangeRequest testRequest = new MakeExchangeRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setRequiredCurrency("RUB");
        testRequest.setGivenAmount(50000.0);

        mockMvc.perform(post("/transactional/exchange")
                        .content(mapper.writeValueAsString(testRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("not enough currency in : EUR"));
    }

    @Test
    void getBalanceByCurrencyTest() throws Exception {

        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setParticipantId(2L);
        balanceRequest.setGivenCurrency("EUR");

        makeDepositingRequest(2);

        TransactionRequest depositing = new TransactionRequest();
        depositing.setParticipantId(2L);
        depositing.setGivenCurrency("RUB");
        depositing.setGivenAmount(2000.0);

        MakeExchangeRequest buying = new MakeExchangeRequest();
        buying.setParticipantId(2L);
        buying.setGivenCurrency("RUB");
        buying.setRequiredCurrency("EUR");
        buying.setGivenAmount(1500.0);

        MakeExchangeRequest selling = new MakeExchangeRequest();
        selling.setParticipantId(2L);
        selling.setGivenCurrency("EUR");
        selling.setRequiredCurrency("RUB");
        selling.setGivenAmount(20.0);

        TransactionRequest withdrawal = new TransactionRequest();
        withdrawal.setParticipantId(2L);
        withdrawal.setGivenCurrency("EUR");
        withdrawal.setGivenAmount(5.0);

        service.depositing(depositing);
        service.exchange(buying);
        service.exchange(selling);
        service.withdrawal(withdrawal);

        mockMvc.perform(get("/transactional/get")
                .content(mapper.writeValueAsString(balanceRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("currencyBalance").value(195.8322725));
    }

    @Test
    void getBadBalanceByCurrencyTest() throws Exception {

        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setParticipantId(2L);
        balanceRequest.setGivenCurrency("EURO"); // Проверка на валюту на которой нет транзакций

        mockMvc.perform(get("/transactional/get")
                        .content(mapper.writeValueAsString(balanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("currencyBalance").value(0));
    }
}

