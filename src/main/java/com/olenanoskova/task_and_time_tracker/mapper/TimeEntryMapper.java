package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TimeEntryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TimeEntry;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimeEntryMapper {

    // DTO → Domain (Create)
    public TimeEntry toDomain(@Valid TimeEntryCreateRequestDto dto) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTaskId(dto.getTaskId());
        timeEntry.setUserId(dto.getUserId());
        timeEntry.setStartTime(dto.getStartTime());
        timeEntry.setEndTime(dto.getEndTime());
        timeEntry.setCreatedAt(Instant.now());
        timeEntry.setUpdatedAt(Instant.now());
        return timeEntry;
    }

    // Domain → Entity
    public TimeEntryEntity toEntity(TimeEntry timeEntry) {
        TimeEntryEntity entity = new TimeEntryEntity();
        entity.setId(timeEntry.getId());
        entity.setTaskId(timeEntry.getTaskId());
        entity.setUserId(timeEntry.getUserId());
        entity.setStartTime(timeEntry.getStartTime());
        entity.setEndTime(timeEntry.getEndTime());
        entity.setDurationSeconds(timeEntry.getDurationSeconds());
        entity.setCreatedAt(timeEntry.getCreatedAt());
        entity.setUpdatedAt(timeEntry.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public TimeEntry toDomain(TimeEntryEntity entity) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(entity.getId());
        timeEntry.setTaskId(entity.getTaskId());
        timeEntry.setUserId(entity.getUserId());
        timeEntry.setStartTime(entity.getStartTime());
        timeEntry.setEndTime(entity.getEndTime());
        timeEntry.setDurationSeconds(entity.getDurationSeconds());
        timeEntry.setCreatedAt(entity.getCreatedAt());
        timeEntry.setUpdatedAt(entity.getUpdatedAt());
        return timeEntry;
    }

    // Domain → Response DTO
    public TimeEntryResponseDto toDto(TimeEntry timeEntry) {
        TimeEntryResponseDto dto = new TimeEntryResponseDto();
        dto.setId(timeEntry.getId());
        dto.setTaskId(timeEntry.getTaskId());
        dto.setUserId(timeEntry.getUserId());
        dto.setStartTime(timeEntry.getStartTime());
        dto.setEndTime(timeEntry.getEndTime());
        dto.setDurationSeconds(timeEntry.getDurationSeconds());
        dto.setCreatedAt(timeEntry.getCreatedAt());
        dto.setUpdatedAt(timeEntry.getUpdatedAt());
        return dto;
    }
}
