package com.olenanoskova.task_and_time_tracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class ProjectMemberCreateRequestDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Member role is required")
    @Valid
    @JsonProperty("member_role")
    private MemberRoleDto memberRoleDto;

}
