package com.example.stockmarket.dao.database;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.dao.StockMarketDao;
import com.example.stockmarket.dao.mapper.StockMarketMapper;
import com.example.stockmarket.entity.Profit;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StockMarketDatabaseDao implements StockMarketDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Profit> getProfit(StockMarketRequest stockMarketRequest) {
        Map<String, Object> values = new HashMap<>();

        SqlBuilder query1 = new SqlBuilder();
        query1.select("given_currency, commission")
                .from("transaction")
                .where("operation_type_id = :operationTypeId");
        values.put("operationTypeId", 2);

        if (stockMarketRequest.getCurrency() != null) {
            query1.where("given_currency = :currency");
            values.put("currency", stockMarketRequest.getCurrency());
        }
        if (stockMarketRequest.getAfter() != null) {
            query1.where("date >= :after");
            values.put("after", stockMarketRequest.getAfter());
        }
        if (stockMarketRequest.getBefore() != null) {
            query1.where("date <= :before");
            values.put("before", stockMarketRequest.getBefore());
        }

        String firstSubQueryPart = query1.build();

        SqlBuilder query2 = new SqlBuilder();
        query2.select("concat(received_currency, given_currency), commission")
                .from("transaction")
                .where("operation_type_id != :operationTypeId");
        values.put("operationTypeId", 2);

        if (stockMarketRequest.getCurrency() != null) {
            query2.where("(given_currency = :currency or received_currency = :currency)");
            values.put("currency", stockMarketRequest.getCurrency());
        }
        if (stockMarketRequest.getAfter() != null) {
            query2.where("date >= :after");
            values.put("after", stockMarketRequest.getAfter());
        }
        if (stockMarketRequest.getBefore() != null) {
            query2.where("date <= :before");
            values.put("before", stockMarketRequest.getBefore());
        }

        String secondSubQueryPart = query2.build();

        SqlBuilder mainQuery = new SqlBuilder();
        String subQuery = mainQuery.buildSubQuery(mainQuery.unionAll(firstSubQueryPart, secondSubQueryPart)); // собираем подзапрос
        String sql = mainQuery.select("given_currency as currency, SUM(commission) as sum")
                .from(subQuery)
                .addAlias("currency")
                .groupBy("given_currency")
                .build();

        return namedParameterJdbcTemplate.query(sql, values, new StockMarketMapper());
    }
}
