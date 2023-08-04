package com.example.stockmarket.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ApplicationProperties {
    @Value("${currency.service.url}")
    private String currencyServiceUrl;
    @Value("${currency.service.key}")
    private String currencyServiceKey;
}
