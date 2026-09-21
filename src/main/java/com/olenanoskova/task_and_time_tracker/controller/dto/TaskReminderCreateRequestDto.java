package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskReminderCreateRequestDto {

    @NotNull(message = "Reminder timestamp cannot be null")
    private Instant remindAt;

    @NotNull(message = "Creator ID is required")
    private UUID createdBy;

    @NotBlank(message = "Reminder message cannot be blank")
    @Size(max = 500, message = "Reminder message must be <= 500 characters")
    private String message;

}
