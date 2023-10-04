package com.example.stockmarket.controller;

import com.example.stockmarket.controller.request.participantRequest.CreateParticipantRequest;
import com.example.stockmarket.controller.request.participantRequest.GetParticipantByIdRequest;
import com.example.stockmarket.controller.request.participantRequest.GetParticipantByNameRequest;
import com.example.stockmarket.controller.request.participantRequest.UpdateParticipantRequest;
import com.example.stockmarket.controller.response.ErrorResponse;
import com.example.stockmarket.controller.response.ParticipantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/participant")
@Tag(name = "Функционал участника")
public interface ParticipantController {
    @Operation(summary = "Создание участника")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Участник создан",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Участник не создан. Ошибка валидации",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })

    })
    @PostMapping("/create")
    ParticipantResponse createParticipant(@RequestBody CreateParticipantRequest createParticipantRequest);

    @Operation(summary = "Поиск участника по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Участник найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getById")
    ResponseEntity<ParticipantResponse> getParticipantById(@RequestBody GetParticipantByIdRequest getParticipantByIdRequest);

    @Operation(summary = "Поиск участника по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Участник найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getByName")
    ResponseEntity<ParticipantResponse> getParticipantByName(@RequestBody GetParticipantByNameRequest getParticipantByNameRequest);

    @Operation(summary = "Изменение данных участника", description = "Позволяет изменить данные существующего участника")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Участник изменён",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantResponse.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Участник не найден",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Участник не изменён. Ошибка валидации",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
    })
    @PostMapping("/edit")
    ParticipantResponse editParticipant(@RequestBody UpdateParticipantRequest updateParticipantRequest);
}
