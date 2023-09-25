package com.example.stockmarket.controller.request.participantRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Сущность запроса на изменение данных участника")
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateParticipantRequest extends CreateParticipantRequest {
}
