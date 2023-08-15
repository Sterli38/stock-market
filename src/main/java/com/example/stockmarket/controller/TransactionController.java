package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.GetBalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.GetTransactionsRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionIdResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactional")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/makeDepositing")
    public TransactionIdResponse makeDepositing(@RequestBody MakeDepositingRequest makeDepositingRequest) {
        Transaction transaction = service.depositing(makeDepositingRequest);
        return convertToTransactionIdResponse(transaction);
    }

    @GetMapping("/withdrawal")
    public TransactionIdResponse makeWithdrawal(@RequestBody MakeWithdrawalRequest makeWithdrawalRequest) {
        Transaction transaction = service.withdrawal(makeWithdrawalRequest);
        return convertToTransactionIdResponse(transaction);
    }

    @PostMapping("/exchange")
    public TransactionIdResponse exchange(@RequestBody MakeExchangeRequest makeExchangeRequest) {
        Transaction transaction = service.exchange(makeExchangeRequest);
        return convertToTransactionIdResponse(transaction);
    }

    @GetMapping("/getBalanceByCurrncy")
    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody GetBalanceRequest getBalanceRequest) {
        return convertToBalanceResponse(service.getBalanceByCurrency(getBalanceRequest.getParticipantId(), getBalanceRequest.getCurrency()));
    }

    @GetMapping("/getTransactions")
    public List<TransactionResponse> getTransactionsByFilter(@RequestBody GetTransactionsRequest getTransactionsRequest) {
        return service.getTransactionsByFilter(getTransactionsRequest).stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionIdResponse convertToTransactionIdResponse(Transaction transaction) {
        TransactionIdResponse transactionalResponse = new TransactionIdResponse();
        transactionalResponse.setTransactionId(transaction.getId());
        return transactionalResponse;
    }

    private BalanceByCurrencyResponse convertToBalanceResponse(double sum) {
        BalanceByCurrencyResponse balanceByCurrencyResponse = new BalanceByCurrencyResponse();
        balanceByCurrencyResponse.setCurrencyBalance(sum);
        return balanceByCurrencyResponse;
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();

        transactionResponse.setOperationType(transaction.getOperationType());
        transactionResponse.setDate(transaction.getDate());
        transactionResponse.setGivenCurrency(transaction.getGivenCurrency());
        transactionResponse.setGivenAmount(transaction.getGivenAmount());
        transactionResponse.setReceivedCurrency(transaction.getReceivedCurrency());
        transactionResponse.setReceivedAmount(transaction.getReceivedAmount());
        transactionResponse.setParticipant(transaction.getParticipant());
        transactionResponse.setCommission(transaction.getCommission());

        return transactionResponse;
    }
}