package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.NotificationResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.NotificationEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.NotificationStatusEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Notification;
import com.olenanoskova.task_and_time_tracker.service.model.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    // Entity → Domain
    public Notification toDomain(NotificationEntity entity) {
        Notification notification = new Notification();
        notification.setId(entity.getId());
        notification.setUserId(entity.getUserId());
        notification.setProjectId(entity.getProjectId());
        notification.setTaskId(entity.getTaskId());
        notification.setStatus(NotificationStatus.valueOf(entity.getStatus().name()));
        notification.setType(entity.getType());
        notification.setMessage(entity.getMessage());
        notification.setRead(entity.getRead() != null && entity.getRead());
        notification.setScheduledAt(entity.getScheduledAt());
        notification.setSentAt(entity.getSentAt());
        notification.setCreatedAt(entity.getCreatedAt());
        notification.setUpdatedAt(entity.getUpdatedAt());
        return notification;
    }

    // Domain → Entity
    public NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        entity.setUserId(notification.getUserId());
        entity.setProjectId(notification.getProjectId());
        entity.setTaskId(notification.getTaskId());
        entity.setStatus(NotificationStatusEntity.valueOf(notification.getStatus().name()));
        entity.setType(notification.getType());
        entity.setMessage(notification.getMessage());
        entity.setRead(notification.isRead());
        entity.setScheduledAt(notification.getScheduledAt());
        entity.setSentAt(notification.getSentAt());
        entity.setCreatedAt(notification.getCreatedAt());
        entity.setUpdatedAt(notification.getUpdatedAt());
        return entity;
    }

    // Domain → Response DTO
    public NotificationResponseDto toDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setProjectId(notification.getProjectId());
        dto.setTaskId(notification.getTaskId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.isRead());
        dto.setScheduledAt(notification.getScheduledAt());
        dto.setSentAt(notification.getSentAt());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }
}

