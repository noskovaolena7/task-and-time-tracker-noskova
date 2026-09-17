package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskHistoryResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskHistoryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;
import org.springframework.stereotype.Component;

@Component
public class TaskHistoryMapper {

    // Entity → Domain
    public TaskHistory toDomain(TaskHistoryEntity entity) {
        TaskHistory history = new TaskHistory();
        history.setId(entity.getId());
        history.setTaskId(entity.getTaskId());
        history.setUserId(entity.getUserId());
        history.setFieldName(entity.getField());
        history.setOldValue(entity.getOldValue());
        history.setNewValue(entity.getNewValue());
        history.setChangedAt(entity.getChangedAt());
        return history;
    }

    // Domain → Entity
    public TaskHistoryEntity toEntity(TaskHistory history) {
        TaskHistoryEntity entity = new TaskHistoryEntity();
        entity.setId(history.getId());
        entity.setTaskId(history.getTaskId());
        entity.setUserId(history.getUserId());
        entity.setField(history.getFieldName());
        entity.setOldValue(history.getOldValue());
        entity.setNewValue(history.getNewValue());
        entity.setChangedAt(history.getChangedAt());
        return entity;
    }

    // Domain → Response DTO
    public TaskHistoryResponseDto toDto(TaskHistory history) {
        TaskHistoryResponseDto dto = new TaskHistoryResponseDto();
        dto.setId(history.getId());
        dto.setTaskId(history.getTaskId());
        dto.setUserId(history.getUserId());
        dto.setFieldChanged(history.getFieldName());
        dto.setOldValue(history.getOldValue());
        dto.setNewValue(history.getNewValue());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }

    public java.util.List<TaskHistoryResponseDto> toDtoList(java.util.List<TaskHistory> histories) {
        return histories.stream().map(this::toDto).toList();
    }
}
