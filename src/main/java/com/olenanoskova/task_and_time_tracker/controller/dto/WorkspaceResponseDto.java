package com.olenanoskova.task_and_time_tracker.controller.dto;

import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceTypeEntity;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class WorkspaceResponseDto {

    private UUID id;

    private String name;

    private WorkspaceTypeEntity type;

    private UUID ownerId;

    private UUID companyId;

    private Instant createdAt;

    private Instant updatedAt;
}
