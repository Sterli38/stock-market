package com.example.stockmarket.service.stockMarketService;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.dao.StockMarketDao;
import com.example.stockmarket.entity.Profit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMarketService {
    private final StockMarketDao stockMarketDao;

    public List<Profit> getProfit(StockMarketRequest stockMarketRequest) {
        return stockMarketDao.getProfit(stockMarketRequest);
    }
}
