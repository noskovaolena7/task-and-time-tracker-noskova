package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCompanyRole {
    private UUID id;
    private UUID userId;
    private UUID companyId;
    private MemberRole role;
    private UUID invitedBy;
    private Instant createdAt;
    private Instant updatedAt;
}

