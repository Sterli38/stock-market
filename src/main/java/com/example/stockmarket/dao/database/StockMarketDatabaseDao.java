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

        List<String> conditions = new ArrayList<>();
        conditions.add("operation_type_id = :id");
        conditions.add("given_currency = :currency");
        conditions.add("date >= :after");
        conditions.add("date <= :before");

        List<String> conditionsValue = new ArrayList<>();
        conditionsValue.add("id");
        conditionsValue.add("currency");
        conditionsValue.add("after");
        conditionsValue.add("before");

        List<Object> values1 = new ArrayList<>();
        values1.add(2);
        values1.add(stockMarketRequest.getCurrency());
        values1.add(stockMarketRequest.getAfter());
        values1.add(stockMarketRequest.getBefore());


        SqlBuilder query1 = new SqlBuilder();
        String firstSubQueryPart = query1.select("given_currency, commission")
                .from("transaction")
                .buildCondition(conditions, conditionsValue, values1) // собираем условия where
                .getSql(); // первая часть подзапроса

        values = query1.getMap();

        SqlBuilder query2 = new SqlBuilder();
        conditions.set(0, "operation_type_id != :id");
        conditions.set(1, "(given_currency = :currency or received_currency = :currency)");

        String secondSubQueryPart = query2.select("concat(received_currency, given_currency), commission")
                .from("transaction")
                .buildCondition(conditions, conditionsValue, values1)
                .getSql();// вторая часть подзапроса

        SqlBuilder mainQuery = new SqlBuilder();
        String subQuery = mainQuery.buildSubQuery(mainQuery.union(firstSubQueryPart, secondSubQueryPart)); // собираем подзапрос
        String sql = mainQuery.select("given_currency as currency, SUM(commission) as sum")
                .from(subQuery)
                .addAlias("currency")
                .condition(" GROUP BY ", "given_currency")
                .getSql(); // Запрос целиком

        return namedParameterJdbcTemplate.query(sql, values, new StockMarketMapper());
    }
}
