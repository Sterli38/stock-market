package com.example.stockmarket.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {

    }
}
