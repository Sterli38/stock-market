package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.GetBalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionResponse;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.transactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactional")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/makeDepositing")
    public TransactionResponse makeDepositing(@RequestBody MakeDepositingRequest makeDepositingRequest) {
        Transaction transaction = service.depositing(makeDepositingRequest);
        return convertToTransactionalResponse(transaction);
    }

    @GetMapping("/withdrawal")
    public TransactionResponse makeWithdrawal(@RequestBody MakeWithdrawalRequest makeWithdrawalRequest) {
        Transaction transaction = service.withdrawal(makeWithdrawalRequest);
        return convertToTransactionalResponse(transaction);
    }

    @PostMapping("/exchange")
    public TransactionResponse exchange(@RequestBody MakeExchangeRequest makeExchangeRequest) {
        Transaction transaction = service.exchange(makeExchangeRequest);
        return convertToTransactionalResponse(transaction);
    }

    @GetMapping("/get")
    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody GetBalanceRequest getBalanceRequest) {
        return convertToBalanceResponse(service.getBalanceByCurrency(getBalanceRequest.getParticipantId(), getBalanceRequest.getCurrency()));
    }

    private TransactionResponse convertToTransactionalResponse(Transaction transaction) {
        TransactionResponse transactionalResponse = new TransactionResponse();
        transactionalResponse.setTransactionId(transaction.getId());
        return transactionalResponse;
    }

    private BalanceByCurrencyResponse convertToBalanceResponse(double sum) {
        BalanceByCurrencyResponse balanceByCurrencyResponse = new BalanceByCurrencyResponse();
        balanceByCurrencyResponse.setCurrencyBalance(sum);
        return balanceByCurrencyResponse;
    }
}