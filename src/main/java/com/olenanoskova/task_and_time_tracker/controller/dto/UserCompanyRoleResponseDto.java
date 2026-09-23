package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserCompanyRoleResponseDto {

    @NotNull(message = "Role ID cannot be null")
    private UUID id;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Company ID cannot be null")
    private UUID companyId;

    @NotNull(message = "Role cannot be null")
    @Valid
    private RoleDto role;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // may be null if the role has not been edited yet
    private Instant updatedAt;
}
