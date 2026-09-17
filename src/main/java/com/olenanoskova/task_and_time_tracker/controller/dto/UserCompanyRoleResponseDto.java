package com.olenanoskova.task_and_time_tracker.controller.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@NoArgsConstructor
public class UserCompanyRoleResponseDto {

    private UUID id;
    private UUID userId;
    private UUID companyId;
    private RoleDto role;
    private Instant createdAt;
    private Instant updatedAt;
}
