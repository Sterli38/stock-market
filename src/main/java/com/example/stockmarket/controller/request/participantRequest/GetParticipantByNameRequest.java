package com.example.stockmarket.controller.request.participantRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Сущность запроса на получение участника по имени")
@Data
public class GetParticipantByNameRequest {
    @NotNull
    @Schema(description = "Имя участника для поиска")
    private String name;
}
