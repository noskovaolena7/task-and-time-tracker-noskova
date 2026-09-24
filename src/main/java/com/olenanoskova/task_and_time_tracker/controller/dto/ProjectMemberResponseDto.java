package com.olenanoskova.task_and_time_tracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("member_role")
    private MemberRoleDto memberRoleDto;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // may be null if the member has not been edited yet
    private Instant updatedAt;

    // display data, may be null if the user was deleted
    private String firstName;

    private String lastName;

    private String email;

}
