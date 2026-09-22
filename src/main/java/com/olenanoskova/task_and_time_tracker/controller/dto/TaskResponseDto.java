package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskResponseDto {
    @NotNull(message = "Task ID cannot be null")
    private UUID id;

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    @NotBlank(message = "Task title cannot be blank")
    @Size(max = 200, message = "Task title must be <= 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must be <= 2000 characters")
    private String description;

    @NotBlank(message = "Task status cannot be blank")
    @Size(max = 50, message = "Task status must be <= 50 characters")
    private String status;

    @NotBlank(message = "Task priority cannot be blank")
    @Size(max = 50, message = "Task priority must be <= 50 characters")
    private String priority;

    @NotNull(message = "Creator ID cannot be null")
    private UUID createdBy;

    // може бути null — задача може бути не призначена
    private UUID assignedTo;

    // може бути null — дедлайн не обов’язковий
    private Instant dueDate;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // може бути null, якщо задачу ще не редагували
    private Instant updatedAt;

    // може бути null, якщо задача не завершена
    private Instant completedAt;

}