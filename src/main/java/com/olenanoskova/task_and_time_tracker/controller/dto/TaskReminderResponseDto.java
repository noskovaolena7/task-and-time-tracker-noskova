package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskReminderResponseDto {

    @NotNull(message = "Reminder ID cannot be null")
    private UUID id;

    @NotNull(message = "Task ID cannot be null")
    private UUID taskId;

    @NotNull(message = "Reminder timestamp cannot be null")
    private Instant remindAt;

    @Size(max = 500, message = "Reminder message must be <= 500 characters")
    private String message;

    @NotNull(message = "Creator ID cannot be null")
    private UUID createdBy;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // may be null if the reminder has not been edited yet
    private Instant updatedAt;
}
