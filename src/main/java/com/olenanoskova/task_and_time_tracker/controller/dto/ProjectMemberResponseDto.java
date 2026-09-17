package com.olenanoskova.task_and_time_tracker.controller.dto;

import java.time.Instant;
import java.util.UUID;

public class ProjectMemberResponseDto {
    private UUID id;
    private UUID projectId;
    private UUID userId;
    private MemberRoleDto memberRoleDto;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public MemberRoleDto getMemberRoleDto() {
        return memberRoleDto;
    }

    public void setMemberRoleDto(MemberRoleDto memberRoleDto) {
        this.memberRoleDto = memberRoleDto;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
