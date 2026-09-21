package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class ProjectDeadlineUpdateRequestDto {


    @NotNull(message = "Deadline timestamp cannot be null")
    private Instant deadline;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;
}
