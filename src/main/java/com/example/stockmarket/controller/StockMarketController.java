package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.stockMarketRequest.StockMarketRequest;
import com.example.stockmarket.controller.response.ErrorResponse;
import com.example.stockmarket.controller.response.StockMarketResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/stockMarket")
@Tag(name = "Функционал биржи", description = "Позволяет получить заработок биржи с комиссии")
public interface StockMarketController {
    @Operation(summary = "Получение прибыли", description = "Получение прибыли биржи с операций на которые накладывается комиссия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прибыль рассчитана",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StockMarketResponse.class)))
                    }),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
//    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getProfit")
    List<StockMarketResponse> get(@RequestBody StockMarketRequest stockMarketRequest);
}
