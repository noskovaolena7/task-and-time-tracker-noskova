package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TimeEntryCreateRequestDto {

    // Optional: task id is taken from the path /tasks/{taskId}/time-entries,
    // the service overwrites any value sent here.
    private UUID taskId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Start time cannot be null")
    private Instant startTime;

    @NotNull(message = "End time cannot be null")
    private Instant endTime;

}
