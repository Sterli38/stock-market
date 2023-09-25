package com.example.stockmarket.controller.request.participantRequest;

import com.example.stockmarket.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

@Schema(description = "Сущность запроса на создание нового участника")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateParticipantRequest extends ParticipantRequest {
    @Schema(description = "Имя участника")
    @NotEmpty
    private String name;
    @Schema(description = "Роли участника")
    @NotNull
    private Set<Role> roles;
    @Schema(description = "Пароль участника")
    @Size(min = 0, max = 30)
    @NotEmpty
    private String password;
    @Schema(description = "Активирован ли участник")
    @NotNull
    private boolean enabled;
    @Schema(description = "Дата создания")
    @NotNull
    private Date creationDate;
}
