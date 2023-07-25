package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.BalanceRequest;
import com.example.stockmarket.controller.request.TransactionRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionalResponse;
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
    public TransactionalResponse makeDepositing(@RequestBody TransactionRequest transactionalRequest) {
        Transaction transaction = convertTransactionalRequest(transactionalRequest);
        transaction.setOperationType(OperationType.DEPOSITING);
        return convertToTransactionalResponse(service.depositing(transaction));
    }

    @PostMapping("/buy")
    public TransactionalResponse buy(@RequestBody TransactionRequest transactionalRequest)  {
        Transaction transaction = convertTransactionalRequest(transactionalRequest);
        transaction.setOperationType(OperationType.BUYING);
        return convertToTransactionalResponse(service.buy(transaction));
    }

    @PostMapping("/sell")
    public TransactionalResponse sell(@RequestBody TransactionRequest transactionalRequest) {
        Transaction transaction = convertTransactionalRequest(transactionalRequest);
        transaction.setOperationType(OperationType.SELLING);
       return convertToTransactionalResponse(service.sell(transaction));
    }

    @GetMapping("/get")
    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody BalanceRequest balanceRequest) {
        return convertToBalanceResponse(service.getBalanceByCurrency(convertBalanceRequest(balanceRequest)));
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

    private Transaction convertBalanceRequest(BalanceRequest request) {
        Transaction transaction = new Transaction();
        transaction.setParticipantId(request.getParticipantId());
        transaction.setReceivedCurrency(request.getReceivedCurrency());

        return transaction;
    }

    private TransactionalResponse convertToTransactionalResponse (Transaction transaction) {
        TransactionalResponse transactionalResponse = new TransactionalResponse();
        transactionalResponse.setTransactionId(transaction.getId());
        return transactionalResponse;
    }

    private BalanceByCurrencyResponse convertToBalanceResponse(double sum) {
        BalanceByCurrencyResponse balanceByCurrencyResponse = new BalanceByCurrencyResponse();
        balanceByCurrencyResponse.setCurrencyBalance(sum);
        return balanceByCurrencyResponse;
    }
}
