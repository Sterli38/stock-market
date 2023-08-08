package com.example.stockmarket.service.stockMarketService;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.dao.StockMarketDao;
import com.example.stockmarket.dao.database.SqlBuilder;
import com.example.stockmarket.entity.Profit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockMarketService {
    private final StockMarketDao stockMarketDao;

    public double getProfit(StockMarketRequest stockMarketRequest) {
        double result = 0;
        Map<String, Object> values = new HashMap<>();
        SqlBuilder sqlBuilder = new SqlBuilder();

        sqlBuilder.select("commission")
                .from("history");
        if(stockMarketRequest.getAfter() != null) {
            sqlBuilder.where("date >= :after");
            values.put("after", stockMarketRequest.getAfter());
        }
        if(stockMarketRequest.getBefore() != null) {
            sqlBuilder.where("date <= :before");
            values.put("before", stockMarketRequest.getBefore());
        }

        sqlBuilder.build();
        String sql = sqlBuilder.getSQL();

        List<Profit> profitList = stockMarketDao.getProfit(sql, values);

        for(Profit element: profitList) {
            result += element.getAmountProfit();
        }
        return result;
    }
}
