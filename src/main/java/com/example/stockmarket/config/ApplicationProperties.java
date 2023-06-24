package com.example.stockmarket.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Getter
public class ApplicationProperties {
    @Value("${currency.service.url}")
    private String currencyServiceUrl;
    @Value("${currency.service.key}")
    private String currencyServiceKey;
}
