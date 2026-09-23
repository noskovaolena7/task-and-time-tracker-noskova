package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TimeEntryResponseDto {

    @NotNull(message = "Time entry ID cannot be null")
    private UUID id;

    @NotNull(message = "Task ID cannot be null")
    private UUID taskId;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Start time cannot be null")
    private Instant startTime;

    @NotNull(message = "End time cannot be null")
    private Instant endTime;

    @PositiveOrZero(message = "Duration must be >= 0 seconds")
    private long durationSeconds;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // may be null if the entry has not been edited yet
    private Instant updatedAt;

}