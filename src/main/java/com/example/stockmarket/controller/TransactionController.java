package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.TransactionRequest;
import com.example.stockmarket.entity.Transaction;
import com.example.stockmarket.service.replenishmentService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactional")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/add")
    public void replenishment(@RequestBody TransactionRequest transactionalRequest) {
        service.replenishment(convertTransactionalRequest(transactionalRequest));
    }

    @PostMapping("/buy")
    public void buy(@RequestBody TransactionRequest transactionalRequest) {
        service.buy(convertTransactionalRequest(transactionalRequest));
    }

    @PostMapping("/sell")
    public void sell(@RequestBody TransactionRequest transactionalRequest) {
        service.sell(convertTransactionalRequest(transactionalRequest));
    }

    @GetMapping("/get")
    public void getBalanceByCurrency(@RequestBody TransactionRequest transactionalRequest) {
        service.getBalanceByCurrency(convertTransactionalRequest(transactionalRequest));
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
}
