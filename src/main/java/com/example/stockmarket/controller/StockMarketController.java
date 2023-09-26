package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.controller.response.ErrorResponse;
import com.example.stockmarket.controller.response.StockMarketResponse;
import com.example.stockmarket.entity.Profit;
import com.example.stockmarket.service.stockMarketService.StockMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stockMarket")
@Tag(name = "Функционал биржи", description = "Позволяет получить заработок биржи с комиссии")
public class StockMarketController {
    private final StockMarketService stockMarketService;

    @Operation(summary = "Получение прибыли", description = "Получение прибыли биржи с операций на которые накладывается комиссия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прибыль рассчитана"),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @GetMapping("/getProfit")
    public List<StockMarketResponse> get(@RequestBody StockMarketRequest stockMarketRequest) {
        return stockMarketService.getProfit(stockMarketRequest).stream()
                .map(this::convertProfit)
                .toList();
    }

    private StockMarketResponse convertProfit(Profit profit) {
        StockMarketResponse stockMarketResponse = new StockMarketResponse();
        stockMarketResponse.setCurrency(profit.getCurrency());
        stockMarketResponse.setAmountProfit(profit.getAmountProfit());
        return stockMarketResponse;
    }
}
