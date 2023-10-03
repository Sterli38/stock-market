package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.transactionRequest.*;
import com.example.stockmarket.controller.response.BalanceByCurrencyResponse;
import com.example.stockmarket.controller.response.ErrorResponse;
import com.example.stockmarket.controller.response.StockMarketResponse;
import com.example.stockmarket.controller.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@RequestMapping("/transactional")
@Tag(name = "Функционал счёта пользователя на бирже", description = "Позволяет производить операции с валютами на бирже")
public interface TransactionController {
    @Operation(summary = "Пополнение счёта", description = "Операция пополнения счёта участника в выбранной валюте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счёт пополнен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/makeDepositing")
    TransactionResponse makeDepositing(@RequestBody MakeDepositingRequest makeDepositingRequest);

    @Operation(summary = "Вывод средств со счёта", description = "Операция вывода средств со счёта участника в выбранной валюте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вывод успешно выполнен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/withdrawal")
    TransactionResponse makeWithdrawal(@RequestBody MakeWithdrawalRequest makeWithdrawalRequest);

    @Operation(summary = "Обмен валют", description = "Операция обмена валюты на валюту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция обмена была выполнена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/exchange")
    TransactionResponse exchange(@RequestBody MakeExchangeRequest makeExchangeRequest);

    @Operation(summary = "Получение баланса по валюте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс получен",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BalanceByCurrencyResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/getBalanceByCurrency")
    BalanceByCurrencyResponse getBalanceByCurrency(@RequestBody GetBalanceRequest getBalanceRequest);

    @Operation(summary = "Получение транзакций", description = "Получение транзакций пользователя по заданному фильтру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакции получены",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TransactionResponse.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Валидация запроса не пройдена",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/getTransactions")
    List<TransactionResponse> getTransactionsByFilter(@RequestBody GetTransactionsRequest getTransactionsRequest);
}
