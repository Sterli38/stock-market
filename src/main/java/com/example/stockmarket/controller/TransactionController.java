package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.InputOutputRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionResponse;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.transactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactional")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/makeDepositing")
    public TransactionResponse makeDepositing(@RequestBody InputOutputRequest inputOutputRequest) {
        Transaction transaction = service.depositing(inputOutputRequest);
        return convertToTransactionalResponse(transaction);
    }

    @GetMapping("/withdrawal")
    public TransactionResponse makeWithdrawal(@RequestBody InputOutputRequest inputOutputRequest) {
        Transaction transaction = service.withdrawal(inputOutputRequest);
        return convertToTransactionalResponse(transaction);
    }

    @PostMapping("/exchange")
    public TransactionResponse exchange(@RequestBody MakeExchangeRequest makeExchangeRequest)  {
        Transaction transaction = service.exchange(makeExchangeRequest);
        return convertToTransactionalResponse(transaction);
    }

    @GetMapping("/get")
    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody TransactionRequest transactionRequest) {
        return convertToBalanceResponse(service.getBalanceByCurrency(transactionRequest.getParticipantId(), transactionRequest.getGivenCurrency()));
    }

    private TransactionResponse convertToTransactionalResponse (Transaction transaction) {
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
