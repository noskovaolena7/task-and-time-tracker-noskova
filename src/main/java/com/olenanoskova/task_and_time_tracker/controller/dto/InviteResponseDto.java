package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class InviteResponseDto {

    private UUID id;

    private UUID companyId;

    private String code;

    private Instant expiresAt;

    private MemberRoleDto role;
}
