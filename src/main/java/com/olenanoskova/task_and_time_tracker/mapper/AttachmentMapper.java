package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.AttachmentEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Attachment;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AttachmentMapper {

    // DTO → Domain (Create)
    public Attachment toDomain(@Valid AttachmentCreateRequestDto dto) {
        Attachment attachment = new Attachment();
        attachment.setFileName(dto.getFileName());
        attachment.setFileUrl(dto.getFileUrl());
        attachment.setUploadedBy(dto.getUploadedBy());
        attachment.setUploadedAt(Instant.now());
        attachment.setUpdatedAt(Instant.now());
        return attachment;
    }

    // Domain → Entity
    public AttachmentEntity toEntity(Attachment attachment) {
        AttachmentEntity entity = new AttachmentEntity();
        entity.setId(attachment.getId());
        entity.setProjectId(attachment.getProjectId());
        entity.setTaskId(attachment.getTaskId());
        entity.setFileName(attachment.getFileName());
        entity.setFileUrl(attachment.getFileUrl());
        entity.setUploadedBy(attachment.getUploadedBy());
        entity.setUploadedAt(attachment.getUploadedAt());
        entity.setCreatedAt(attachment.getCreatedAt());
        entity.setUpdatedAt(attachment.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public Attachment toDomain(AttachmentEntity entity) {
        Attachment attachment = new Attachment();
        attachment.setId(entity.getId());
        attachment.setProjectId(entity.getProjectId());
        attachment.setTaskId(entity.getTaskId());
        attachment.setFileName(entity.getFileName());
        attachment.setFileUrl(entity.getFileUrl());
        attachment.setUploadedBy(entity.getUploadedBy());
        attachment.setUploadedAt(entity.getUploadedAt());
        attachment.setCreatedAt(entity.getCreatedAt());
        attachment.setUpdatedAt(entity.getUpdatedAt());
        return attachment;
    }

    // Domain → Response DTO
    public AttachmentResponseDto toDto(Attachment attachment) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(attachment.getId());
        dto.setProjectId(attachment.getProjectId());
        dto.setTaskId(attachment.getTaskId());
        dto.setFileName(attachment.getFileName());
        dto.setFileUrl(attachment.getFileUrl());
        dto.setUploadedBy(attachment.getUploadedBy());
        dto.setUploadedAt(attachment.getUploadedAt());
        dto.setUpdatedAt(attachment.getUpdatedAt());
        return dto;
    }

    public Attachment toDomain(UUID projectId, UUID taskId, AttachmentCreateRequestDto dto, UUID uploadedBy) {

        Attachment attachment = new Attachment();

        attachment.setProjectId(projectId);
        attachment.setTaskId(taskId);

        attachment.setFileName(dto.getFileName());
        attachment.setFileUrl(dto.getFileUrl());

        attachment.setUploadedBy(uploadedBy);
        attachment.setUploadedAt(Instant.now());
        attachment.setUpdatedAt(Instant.now());

        return attachment;
    }

}

