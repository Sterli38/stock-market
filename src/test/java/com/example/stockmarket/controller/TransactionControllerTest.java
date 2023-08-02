package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.BalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.service.transactionService.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    TransactionController transactionController;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    TransactionService service;

    @Test
    void MakeDepositingTest() throws Exception {
        TransactionRequest testRequest = new TransactionRequest();
        testRequest.setParticipantId(1L);
        testRequest.setGivenCurrency("EUR");
        testRequest.setGivenAmount(50.0);

        mockMvc.perform(post("/transactional/makeDepositing")
                .content(mapper.writeValueAsString(testRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId").isNumber());
    }

    @Test
    void MakeWithdrawalTest() throws Exception {
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
    void exchangeTest() throws Exception {
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
    void getBalanceByCurrencyTest() throws Exception {
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setParticipantId(1L);
        balanceRequest.setGivenCurrency("EUR");
        log.debug(service.toString());

        TransactionRequest depositing = new TransactionRequest();
        depositing.setParticipantId(1L);
        depositing.setGivenCurrency("EUR");
        depositing.setGivenAmount(50.0);

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

        TransactionRequest withdrawal = new TransactionRequest();
        withdrawal.setParticipantId(1L);
        withdrawal.setGivenCurrency("EUR");
        withdrawal.setGivenAmount(5.0);


        service.depositing(depositing);
        service.exchange(buying);
        service.exchange(selling);
        service.withdrawal(withdrawal);

        mockMvc.perform(get("/transactional/get")
                .content(mapper.writeValueAsString(balanceRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("currencyBalance").value(44.3322725));
    }
}

