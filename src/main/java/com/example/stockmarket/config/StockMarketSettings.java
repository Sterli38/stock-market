package com.example.stockmarket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stock.market")
@Data
public class StockMarketSettings {
    private Double thresholdOfCommissionUsage;
    private String thresholdBaseCurrency;
    private Double commissionPercent;
}
