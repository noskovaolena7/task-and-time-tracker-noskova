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

    // may be null — the task may be unassigned
    private UUID assignedTo;

    // may be null — due date is optional
    private Instant dueDate;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // may be null if the task has not been edited yet
    private Instant updatedAt;

    // may be null if the task is not completed
    private Instant completedAt;

}