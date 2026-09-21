package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserCompanyRoleCreateRequestDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Role is required")
    @Valid
    private RoleDto role;
}
