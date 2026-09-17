package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskStatusEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskPriorityEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Task;
import com.olenanoskova.task_and_time_tracker.service.model.TaskStatus;
import com.olenanoskova.task_and_time_tracker.service.model.TaskPriority;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TaskMapper {

    // DTO → Domain (Create)
    public Task toDomain(@Valid TaskCreateRequestDto dto) {
        Task task = new Task();
        task.setProjectId(dto.getProjectId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));
        task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        task.setCreatedBy(dto.getCreatedBy());
        task.setAssignedTo(dto.getAssignedTo());
        task.setDueDate(dto.getDueDate());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        return task;
    }

    // DTO → Domain (Update)
    public void updateDomain(TaskUpdateRequestDto dto, Task task) {
        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(dto.getStatus()));
        }
        if (dto.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        }
        if (dto.getAssignedTo() != null) {
            task.setAssignedTo(dto.getAssignedTo());
        }
        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate());
        }
        task.setUpdatedAt(Instant.now());
    }

    // Domain → Entity
    public TaskEntity toEntity(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setProjectId(task.getProjectId());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setStatus(TaskStatusEntity.valueOf(task.getStatus().name()));
        entity.setPriority(TaskPriorityEntity.valueOf(task.getPriority().name()));
        entity.setCreatedBy(task.getCreatedBy());
        entity.setAssignedTo(task.getAssignedTo());
        entity.setDueDate(task.getDueDate());
        entity.setCompletedAt(task.getCompletedAt());
        entity.setCreatedAt(task.getCreatedAt());
        entity.setUpdatedAt(task.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public Task toDomain(TaskEntity entity) {
        Task task = new Task();
        task.setId(entity.getId());
        task.setProjectId(entity.getProjectId());
        task.setTitle(entity.getTitle());
        task.setDescription(entity.getDescription());
        task.setStatus(TaskStatus.valueOf(entity.getStatus().name()));
        task.setPriority(TaskPriority.valueOf(entity.getPriority().name()));
        task.setCreatedBy(entity.getCreatedBy());
        task.setAssignedTo(entity.getAssignedTo());
        task.setDueDate(entity.getDueDate());
        task.setCompletedAt(entity.getCompletedAt());
        task.setCreatedAt(entity.getCreatedAt());
        task.setUpdatedAt(entity.getUpdatedAt());
        return task;
    }

    // Domain → Response DTO
    public TaskResponseDto toDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setProjectId(task.getProjectId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        dto.setPriority(task.getPriority().name());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        return dto;
    }
}
