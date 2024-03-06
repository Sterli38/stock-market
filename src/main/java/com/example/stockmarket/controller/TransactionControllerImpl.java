package com.example.stockmarket.controller;

import com.example.stockmarket.config.security.SecurityUtils;
import com.example.stockmarket.controller.request.transactionRequest.GetBalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.GetTransactionsRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionResponse;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.exception.NoAccessToPerformOperation;
import com.example.stockmarket.exception.NoCurrencyForAmountException;
import com.example.stockmarket.service.transactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionControllerImpl implements TransactionController {
    private final TransactionService service;
    private final SecurityUtils securityUtils;

    public TransactionResponse makeDepositing(@RequestBody MakeDepositingRequest makeDepositingRequest) {
        if(!securityUtils.isOperationAvailableForCurrentParticipant(makeDepositingRequest.getParticipantId())) {
            throw new NoAccessToPerformOperation("depositing another participant account");
        }
        Transaction transaction = service.depositing(makeDepositingRequest);
        return convertToTransactionIdResponse(transaction);
    }

    public TransactionResponse makeWithdrawal(@RequestBody MakeWithdrawalRequest makeWithdrawalRequest) {
        if(!securityUtils.isOperationAvailableForCurrentParticipant(makeWithdrawalRequest.getParticipantId())) {
            throw new NoAccessToPerformOperation("withdrawal from another participant account");
        }
        Transaction transaction = service.withdrawal(makeWithdrawalRequest);
        return convertToTransactionIdResponse(transaction);
    }

    public TransactionResponse exchange(@RequestBody MakeExchangeRequest makeExchangeRequest) {
        if(!securityUtils.isOperationAvailableForCurrentParticipant(makeExchangeRequest.getParticipantId())) {
            throw new NoAccessToPerformOperation("exchange from another participant account");
        }
        Transaction transaction = service.exchange(makeExchangeRequest);
        return convertToTransactionIdResponse(transaction);
    }

    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody GetBalanceRequest getBalanceRequest) {
        if(!securityUtils.isOperationAvailableForCurrentParticipant(getBalanceRequest.getParticipantId())) {
            throw new NoAccessToPerformOperation("receive the balance of another participant account");
        }
        return convertToBalanceResponse(service.getBalanceByCurrency(getBalanceRequest.getParticipantId(), getBalanceRequest.getCurrency()));
    }

    public List<TransactionResponse> getTransactionsByFilter(@RequestBody GetTransactionsRequest getTransactionsRequest) {
        if(!securityUtils.isOperationAvailableForCurrentParticipant(getTransactionsRequest.getParticipantId())) {
            throw new NoAccessToPerformOperation("receive another participant transactions");
        }
        if (isOperationNotApplicableForHistory(getTransactionsRequest.getReceivedMaxAmount(), getTransactionsRequest.getReceivedMinAmount(), getTransactionsRequest.getReceivedCurrencies(), getTransactionsRequest.getGivenMaxAmount(), getTransactionsRequest.getGivenMinAmount(), getTransactionsRequest.getGivenCurrencies())) {
            throw new NoCurrencyForAmountException();
        }
        return service.getTransactionsByFilter(getTransactionsRequest).stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse convertToTransactionIdResponse(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setId(transaction.getId());
        return transactionResponse;
    }

    private BalanceByCurrencyResponse convertToBalanceResponse(double sum) {
        BalanceByCurrencyResponse balanceByCurrencyResponse = new BalanceByCurrencyResponse();
        balanceByCurrencyResponse.setCurrencyBalance(sum);
        return balanceByCurrencyResponse;
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();

        transactionResponse.setId(transaction.getId());
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

    /**
     * Проверка, что сумма не ведена без валюты для операции
     *
     * @return
     */
    private boolean isOperationNotApplicableForHistory(Double receivedMaxAmount, Double receivedMinAmount, List<String> receivedCurrency, Double givenMaxAmount, Double givenMinAmount, List<String> givenCurrency) {
        return (((receivedMaxAmount != null || receivedMinAmount != null) && receivedCurrency == null) || ((givenMaxAmount != null || givenMinAmount != null) && givenCurrency == null));
    }
}