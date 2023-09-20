package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.controller.request.transactionRequest.GetBalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
import com.example.stockmarket.service.stockMarketService.StockMarketService;
import com.example.stockmarket.service.transactionService.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class StockMarketControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TransactionService transactionService;

    @Test
    public void getTest() throws Exception {
        jdbcTemplate.update("TRUNCATE table transaction");

        StockMarketRequest stockMarketRequest = new StockMarketRequest();
        stockMarketRequest.setCurrency("EUR");

        MakeDepositingRequest depositing = new MakeDepositingRequest();
        depositing.setParticipantId(2L);
        depositing.setReceivedCurrency("RUB");
        depositing.setReceivedAmount(10000.0);// комиссия = 0

        MakeExchangeRequest buying = new MakeExchangeRequest();
        buying.setParticipantId(2L);
        buying.setGivenCurrency("RUB");
        buying.setReceivedCurrency("EUR");
        buying.setGivenAmount(5000.0);// комиссия = 0

        MakeExchangeRequest selling = new MakeExchangeRequest();
        selling.setParticipantId(2L);
        selling.setGivenCurrency("EUR");
        selling.setReceivedCurrency("RUB");
        selling.setGivenAmount(20.0);// комиссия = 1

        MakeWithdrawalRequest withdrawal = new MakeWithdrawalRequest();
        withdrawal.setParticipantId(2L);
        withdrawal.setGivenCurrency("EUR");
        withdrawal.setGivenAmount(5.0);// комиссия = 0.25

        transactionService.depositing(depositing);
        transactionService.exchange(buying);
        transactionService.exchange(selling);
        transactionService.withdrawal(withdrawal);

        mockMvc.perform(get("/stockMarket/getProfit")
                        .content(mapper.writeValueAsString(stockMarketRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].currency").value("EUR"))
                .andExpect(jsonPath("$.[0].amountProfit").value(1.25));

        jdbcTemplate.update("TRUNCATE table transaction");
    }
}
