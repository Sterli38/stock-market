package com.example.stockmarket.controller.response;

import com.example.stockmarket.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Schema(description = "Сущность ответа для функционала пользователя")
@Data
public class ParticipantResponse {
    @Schema(description = "Идентификатор пользователя")
    private Long id;
    @Schema(description = "Имя пользователя")
    private String name;
    @Schema(description = "Роли пользователя")
    private Set<Role> roles;
    @Schema(description = "Дата создания")
    private Long creationDate;
    @Schema(description = "Активирован ли пользователь")
    private boolean enabled;
}
