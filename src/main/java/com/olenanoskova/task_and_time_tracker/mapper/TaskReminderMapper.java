package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskReminderCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskReminderResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskReminderEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TaskReminderMapper {

    // DTO → Domain (Create)
    public TaskReminder toDomain(@Valid TaskReminderCreateRequestDto dto, java.util.UUID taskId) {
        TaskReminder reminder = new TaskReminder();
        reminder.setTaskId(taskId);
        reminder.setRemindAt(dto.getRemindAt());
        reminder.setCreatedBy(dto.getCreatedBy());
        reminder.setMessage(dto.getMessage());
        reminder.setCreatedAt(Instant.now());
        reminder.setUpdatedAt(Instant.now());
        return reminder;
    }

    // DTO → Domain (Update)
    public void updateDomain(TaskReminderCreateRequestDto dto, TaskReminder reminder) {
        if (dto.getRemindAt() != null) {
            reminder.setRemindAt(dto.getRemindAt());
        }
        if (dto.getMessage() != null) {
            reminder.setMessage(dto.getMessage());
        }
        reminder.setUpdatedAt(Instant.now());
    }

    // Domain → Entity
    public TaskReminderEntity toEntity(TaskReminder reminder) {
        TaskReminderEntity entity = new TaskReminderEntity();
        entity.setId(reminder.getId());
        entity.setTaskId(reminder.getTaskId());
        entity.setCreatedBy(reminder.getCreatedBy());
        entity.setRemindAt(reminder.getRemindAt());
        entity.setMessage(reminder.getMessage());
        entity.setCreatedAt(reminder.getCreatedAt());
        entity.setUpdatedAt(reminder.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public TaskReminder toDomain(TaskReminderEntity entity) {
        TaskReminder reminder = new TaskReminder();
        reminder.setId(entity.getId());
        reminder.setTaskId(entity.getTaskId());
        reminder.setCreatedBy(entity.getCreatedBy());
        reminder.setRemindAt(entity.getRemindAt());
        reminder.setMessage(entity.getMessage());
        reminder.setCreatedAt(entity.getCreatedAt());
        reminder.setUpdatedAt(entity.getUpdatedAt());
        return reminder;
    }

    // Domain → Response DTO
    public TaskReminderResponseDto toDto(TaskReminder reminder) {
        TaskReminderResponseDto dto = new TaskReminderResponseDto();
        dto.setId(reminder.getId());
        dto.setTaskId(reminder.getTaskId());
        dto.setRemindAt(reminder.getRemindAt());
        dto.setMessage(reminder.getMessage());
        dto.setCreatedBy(reminder.getCreatedBy());
        dto.setCreatedAt(reminder.getCreatedAt());
        dto.setUpdatedAt(reminder.getUpdatedAt());
        return dto;
    }
}
