package com.example.stockmarket.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Set;

@Data
@Schema(description = "Сущность участника")
public class Participant {
    @Schema(description = "Идентификатор участника")
    private Long id;
    @Schema(description = "Имя участника")
    private String name;
    @ToString.Exclude
    @Schema(description = "Пароль участника")
    private String password;
    @Schema(description = "Роли участника")
    private Set<Role> roles;
    @Schema(description = "Включен ли участник ( не забанен ли )")
    private boolean enabled;
    @Schema(description = "Дата регистрации")
    private Date creationDate;
}
