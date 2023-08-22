package com.example.stockmarket.dao;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.entity.Profit;

import java.util.List;

public interface StockMarketDao {

    List<Profit> getProfit(StockMarketRequest stockMarketRequest);

}
