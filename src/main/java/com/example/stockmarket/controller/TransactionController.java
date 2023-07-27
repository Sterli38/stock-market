package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.BalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.TransactionRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionResponse;
import com.example.stockmarket.entity.OperationType;
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
    public TransactionResponse makeDepositing(@RequestBody TransactionRequest transactionalRequest) {
        Transaction transaction = convertTransactionalRequest(transactionalRequest);
        return convertToTransactionalResponse(service.depositing(transaction));
    }

    @GetMapping("/withdrawal")
    public TransactionResponse withdrawal(@RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = convertTransactionalRequest(transactionRequest);
        return convertToTransactionalResponse(service.withdrawal(transaction));
    }

    @PostMapping("/exchange")
    public TransactionResponse exchange(@RequestBody TransactionRequest transactionalRequest)  {
        Transaction transaction = convertTransactionalRequest(transactionalRequest);
        return convertToTransactionalResponse(service.exchange(transaction));
    }

    @GetMapping("/get")
    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody BalanceRequest balanceRequest) {
        return convertToBalanceResponse(service.getBalanceByCurrency(balanceRequest.getParticipantId(), balanceRequest.getCurrency()));
    }

    private Transaction convertTransactionalRequest(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setDate(request.getDate());
        transaction.setAmount(request.getAmount());
        transaction.setParticipantId(request.getParticipantId());
        transaction.setReceivedCurrency(request.getReceivedCurrency());
        transaction.setGivenCurrency(request.getGivenCurrency());

        return transaction;
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
