package com.olenanoskova.task_and_time_tracker.controller.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectDeadlineResponseDto {


    @NotNull(message = "Deadline ID cannot be null")
    private UUID id;

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    @NotNull(message = "Deadline timestamp cannot be null")
    private Instant deadline;

    @NotNull(message = "Reminder periods list cannot be null")
    @Size(max = 20, message = "Reminder periods list must contain <= 20 items")
    private List<
            @NotBlank(message = "Reminder period cannot be blank")
            @Size(max = 50, message = "Reminder period must be <= 50 characters")
                    String
            > reminderPeriods;

    @NotNull(message = "Creator ID cannot be null")
    private UUID createdBy;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    private Instant updatedAt;
}

