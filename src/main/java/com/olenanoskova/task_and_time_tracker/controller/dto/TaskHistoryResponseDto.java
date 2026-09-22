package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskHistoryResponseDto {

    @NotNull(message = "History ID cannot be null")
    private UUID id;

    @NotNull(message = "Task ID cannot be null")
    private UUID taskId;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotBlank(message = "Changed field name cannot be blank")
    @Size(max = 100, message = "Changed field name must be <= 100 characters")
    private String fieldChanged;

    @Size(max = 2000, message = "Old value must be <= 2000 characters")
    private String oldValue;

    @Size(max = 2000, message = "New value must be <= 2000 characters")
    private String newValue;

    @NotNull(message = "Change timestamp cannot be null")
    private Instant changedAt;
}
