package com.example.stockmarket.controller.request.stockMarketRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Date;

@Schema(description = "Cущность запроса на подсчёт прибыли с комиссий")
@Data
public class StockMarketRequest {
    @Schema(description = "Валюта в которой хотим увидеть сколько у нас прибыли")
    private String currency;
    @Schema(description = "Начало отрезка времени с которого считаем прибыль")
    private Date after;
    @Schema(description = "Конец отрезка времени")
    private Date before;
}
