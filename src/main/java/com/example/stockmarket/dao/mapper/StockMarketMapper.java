package com.example.stockmarket.dao.mapper;

import com.example.stockmarket.entity.Profit;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockMarketMapper implements RowMapper<Profit> {
    @Override
    public Profit mapRow(ResultSet rs, int rowNum) throws SQLException {
        Profit profit = new Profit();

        profit.setCurrency(rs.getString("currency"));
        profit.setAmountProfit(rs.getDouble("sum"));

        return profit;
    }
}
