package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectDeadlineEntity;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProjectDeadlineMapper {

    // DTO → Domain (Create)
    public ProjectDeadline toDomain(@Valid ProjectDeadlineCreateRequestDto dto, java.util.UUID projectId) {
        ProjectDeadline deadline = new ProjectDeadline();
        deadline.setProjectId(projectId);
        deadline.setDeadline(dto.getDeadline());
        deadline.setReminderPeriods(dto.getReminderPeriods());
        deadline.setCreatedBy(dto.getCreatedBy());
        deadline.setCreatedAt(Instant.now());
        deadline.setUpdatedAt(Instant.now());
        return deadline;
    }

    // DTO → Domain (Update)
    public void updateDomain(ProjectDeadlineCreateRequestDto dto, ProjectDeadline deadline) {
        if (dto.getDeadline() != null) {
            deadline.setDeadline(dto.getDeadline());
        }
        if (dto.getReminderPeriods() != null) {
            deadline.setReminderPeriods(dto.getReminderPeriods());
        }
        deadline.setUpdatedAt(Instant.now());
    }

    // Domain → Entity
    public ProjectDeadlineEntity toEntity(ProjectDeadline deadline) {
        ProjectDeadlineEntity entity = new ProjectDeadlineEntity();
        entity.setId(deadline.getId());
        entity.setProjectId(deadline.getProjectId());
        entity.setDeadline(deadline.getDeadline());
        entity.setReminderPeriods(deadline.getReminderPeriods());
        entity.setCreatedBy(deadline.getCreatedBy());
        entity.setCreatedAt(deadline.getCreatedAt());
        entity.setUpdatedAt(deadline.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public ProjectDeadline toDomain(ProjectDeadlineEntity entity) {
        ProjectDeadline deadline = new ProjectDeadline();
        deadline.setId(entity.getId());
        deadline.setProjectId(entity.getProjectId());
        deadline.setDeadline(entity.getDeadline());
        deadline.setReminderPeriods(entity.getReminderPeriods());
        deadline.setCreatedBy(entity.getCreatedBy());
        deadline.setCreatedAt(entity.getCreatedAt());
        deadline.setUpdatedAt(entity.getUpdatedAt());
        return deadline;
    }

    // Domain → Response DTO
    public ProjectDeadlineResponseDto toDto(ProjectDeadline deadline) {
        ProjectDeadlineResponseDto dto = new ProjectDeadlineResponseDto();
        dto.setId(deadline.getId());
        dto.setProjectId(deadline.getProjectId());
        dto.setDeadline(deadline.getDeadline());
        dto.setReminderPeriods(deadline.getReminderPeriods());
        dto.setCreatedBy(deadline.getCreatedBy());
        dto.setCreatedAt(deadline.getCreatedAt());
        dto.setUpdatedAt(deadline.getUpdatedAt());
        return dto;
    }
}

