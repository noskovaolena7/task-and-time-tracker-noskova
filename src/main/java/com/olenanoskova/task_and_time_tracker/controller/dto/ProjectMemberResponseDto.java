package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ProjectMemberResponseDto {

    @NotNull(message = "Project member ID cannot be null")
    private UUID id;

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Member role cannot be null")
    @Valid
    private MemberRoleDto memberRoleDto;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // може бути null, якщо учасника ще не редагували
    private Instant updatedAt;


}
