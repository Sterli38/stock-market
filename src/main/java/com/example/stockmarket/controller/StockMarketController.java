package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.controller.response.StockMarketResponse;
import com.example.stockmarket.entity.Profit;
import com.example.stockmarket.service.stockMarketService.StockMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stockMarket")
public class StockMarketController {
    private final StockMarketService stockMarketService;

    @GetMapping("/getProfit")
    public List<StockMarketResponse> get(@RequestBody StockMarketRequest stockMarketRequest) {
        return stockMarketService.getProfit(stockMarketRequest).stream()
                .map(this::convertProfit)
                .toList();
    }

    private StockMarketResponse convertProfit(Profit profit) {
        StockMarketResponse stockMarketResponse = new StockMarketResponse();
        stockMarketResponse.setCurrency(profit.getCurrency());
        stockMarketResponse.setAmountProfit(profit.getAmountProfit());
        return stockMarketResponse;
    }
}
