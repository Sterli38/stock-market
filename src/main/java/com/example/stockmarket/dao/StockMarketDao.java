package com.example.stockmarket.dao;

import com.example.stockmarket.entity.Profit;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StockMarketDao {

    List<Profit> getProfit(String sql, Map<String, Object> values);

}
