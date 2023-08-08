package com.example.stockmarket.dao.database;

import com.example.stockmarket.dao.StockMarketDao;
import com.example.stockmarket.dao.mapper.StockMarketMapper;
import com.example.stockmarket.entity.Profit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StockMarketDatabaseDao implements StockMarketDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Profit> getProfit(String sql, Map<String, Object> values) {
        return namedParameterJdbcTemplate.query(sql, values, new StockMarketMapper());
    }
}
