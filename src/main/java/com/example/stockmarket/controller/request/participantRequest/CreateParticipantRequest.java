package com.example.stockmarket.controller.request.participantRequest;

import com.example.stockmarket.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateParticipantRequest extends ParticipantRequest {
    @NotEmpty
    private String name;
    @NotNull
    private Set<Role> roles;
    @Size(min = 0, max = 30)
    @NotEmpty
    private String password;
    @NotNull
    private boolean enabled;
    @NotNull
    private Date creationDate;
}
