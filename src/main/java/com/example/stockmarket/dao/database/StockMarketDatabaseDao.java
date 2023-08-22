package com.example.stockmarket.dao.database;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.dao.StockMarketDao;
import com.example.stockmarket.dao.mapper.StockMarketMapper;
import com.example.stockmarket.entity.Profit;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        SqlBuilder sqlBuilder = new SqlBuilder();

        sqlBuilder.select("given_currency as currency, SUM(commission)")
                .from("(")
                .select("given_currency, commission")
                .from("transaction")
                .where("operation_type_id = 2");
        if (stockMarketRequest.getCurrency() != null) {
            sqlBuilder.where("given_currency = :currency");
            values.put("currency", stockMarketRequest.getCurrency());
        }
        if (stockMarketRequest.getAfter() != null) {
            sqlBuilder.where("date >= :after");
            values.put("after", stockMarketRequest.getAfter());
        }
        if (stockMarketRequest.getBefore() != null) {
            sqlBuilder.where("date <= :before");
            values.put("before", stockMarketRequest.getBefore());
        }
        sqlBuilder.build();
        sqlBuilder.condition("UNION", " ");
        String sql1 = sqlBuilder.getSql();

        SqlBuilder sqlBuilder1 = new SqlBuilder();

        sqlBuilder1.select("concat(received_currency, given_currency), commission")
                .from("transaction")
                .where("operation_type_id != 2");
        if (stockMarketRequest.getCurrency() != null) {
            sqlBuilder1.where("(given_currency = :currency or received_currency = :currency)");
            values.put("currency", stockMarketRequest.getCurrency());
        }
        if (stockMarketRequest.getAfter() != null) {
            sqlBuilder1.where("date >= :after");
            values.put("after", stockMarketRequest.getAfter());
        }
        if (stockMarketRequest.getBefore() != null) {
            sqlBuilder1.where("date <= :before");
            values.put("before", stockMarketRequest.getBefore());
        }
        sqlBuilder1.build();
        sqlBuilder1.condition(") as currency GROUP BY ", "given_currency");
        String sql2 = sqlBuilder1.getSql();

        String sql = sql1 + sql2;
        List<Profit> profitList = namedParameterJdbcTemplate.query(sql, values, new StockMarketMapper());
        return profitList;
    }
}
