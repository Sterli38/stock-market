package com.example.stockmarket.controller;

import com.example.stockmarket.service.WebCurrencyService;
import com.example.stockmarket.service.response.WebCurrencyServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TrashController {
    private final WebCurrencyService webCurrencyService;

    @GetMapping("/get")
    public double convert(@RequestParam String from, @RequestParam String in, @RequestParam double amount) {
        return webCurrencyService.convert(from, amount, in);
    }

    @GetMapping("/getget")
    public boolean isValid(@RequestParam String currencyPair) {
        return webCurrencyService.isValid(currencyPair);
    }
}
