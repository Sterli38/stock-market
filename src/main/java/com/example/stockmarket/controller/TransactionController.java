package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.GetBalanceRequest;
import com.example.stockmarket.controller.request.transactionRequest.GetTransactionsRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeDepositingRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeExchangeRequest;
import com.example.stockmarket.controller.request.transactionRequest.MakeWithdrawalRequest;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.TransactionResponse;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.exception.NoCurrencyForAmountException;
import com.example.stockmarket.service.transactionService.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Контроллер транзакций", description = "Позволяет производить операции с валютами на бирже")
public class TransactionController {
    private final TransactionService service;

    @Operation(summary = "Пополнение счёта", description = "Операция пополнения счёта участника в выбранной валюте")
    @PostMapping("/makeDepositing")
    public TransactionResponse makeDepositing(@RequestBody MakeDepositingRequest makeDepositingRequest) {
        Transaction transaction = service.depositing(makeDepositingRequest);
        return convertToTransactionIdResponse(transaction);
    }

    @Operation(summary = "Вывод средств со счёта", description = "Операция вывода средств со счёта участника в выбранной валюте")
    @GetMapping("/withdrawal")
    public TransactionResponse makeWithdrawal(@RequestBody MakeWithdrawalRequest makeWithdrawalRequest) {
        Transaction transaction = service.withdrawal(makeWithdrawalRequest);
        return convertToTransactionIdResponse(transaction);
    }

    @Operation(summary = "Обмен", description = "Операция обмена валюты на валюту")
    @PostMapping("/exchange")
    public TransactionResponse exchange(@RequestBody MakeExchangeRequest makeExchangeRequest) {
        Transaction transaction = service.exchange(makeExchangeRequest);
        return convertToTransactionIdResponse(transaction);
    }

    @Operation(summary = "Получение баланса по валюте")
    @GetMapping("/getBalanceByCurrency")
    public BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody GetBalanceRequest getBalanceRequest) {
        return convertToBalanceResponse(service.getBalanceByCurrency(getBalanceRequest.getParticipantId(), getBalanceRequest.getCurrency()));
    }

    @Operation(summary = "Пополнение транзакций", description = "Получение транзакций пользователя")
    @GetMapping("/getTransactions")
    public List<TransactionResponse> getTransactionsByFilter(@RequestBody GetTransactionsRequest getTransactionsRequest) {
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
     * Проверка что сумма не ведена без валюты для операции
     *
     * @return
     */
    private boolean isOperationNotApplicableForHistory(Double receivedMaxAmount, Double receivedMinAmount, List<String> receivedCurrency, Double givenMaxAmount, Double givenMinAmount, List<String> givenCurrency) {
        return (((receivedMaxAmount != null || receivedMinAmount != null ) && receivedCurrency == null) || ((givenMaxAmount != null || givenMinAmount != null) && givenCurrency == null));
    }
}