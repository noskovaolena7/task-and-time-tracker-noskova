package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.CommentCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.CommentResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.CommentEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Comment;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CommentMapper {

    // DTO → Domain (Create)
    public Comment toDomain(@Valid CommentCreateRequestDto dto) {
        Comment comment = new Comment();
        comment.setUserId(dto.getUserId());
        comment.setText(dto.getText());
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        return comment;
    }

    // Domain → Entity
    public CommentEntity toEntity(Comment comment) {
        CommentEntity entity = new CommentEntity();
        entity.setId(comment.getId());
        entity.setTaskId(comment.getTaskId());
        entity.setUserId(comment.getUserId());
        entity.setText(comment.getText());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setUpdatedAt(comment.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public Comment toDomain(CommentEntity entity) {
        Comment comment = new Comment();
        comment.setId(entity.getId());
        comment.setTaskId(entity.getTaskId());
        comment.setUserId(entity.getUserId());
        comment.setText(entity.getText());
        comment.setCreatedAt(entity.getCreatedAt());
        comment.setUpdatedAt(entity.getUpdatedAt());
        return comment;
    }

    // Domain → Response DTO
    public CommentResponseDto toDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setTaskId(comment.getTaskId());
        dto.setUserId(comment.getUserId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
