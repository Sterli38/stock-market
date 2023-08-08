package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.controller.response.StockMarketResponse;
import com.example.stockmarket.service.stockMarketService.StockMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stockMarket")
public class StockMarketController {
    private final StockMarketService stockMarketService;

    @GetMapping("/getProfit")
    public StockMarketResponse get(@RequestBody StockMarketRequest stockMarketRequest) {
        StockMarketResponse stockMarketResponse = new StockMarketResponse();
        stockMarketResponse.setAmountProfit(stockMarketService.getProfit(stockMarketRequest));
        return stockMarketResponse;
    }
}
