package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommentResponseDto {

    @NotNull(message = "Comment ID cannot be null")
    private UUID id;

    @NotNull(message = "Task ID cannot be null")
    private UUID taskId;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotBlank(message = "Comment text cannot be blank")
    @Size(max = 1000, message = "Comment text must be <= 1000 characters")
    private String text;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // updatedAt може бути null, якщо коментар ще не редагували
    private Instant updatedAt;

}
