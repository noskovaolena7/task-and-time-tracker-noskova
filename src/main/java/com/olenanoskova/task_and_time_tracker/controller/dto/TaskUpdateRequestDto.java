package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;
@Data
public class TaskUpdateRequestDto {

    @Size(min = 2, max = 200, message = "Task title must be between 2 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must be <= 2000 characters")
    private String description;

    @Size(max = 50, message = "Status must be <= 50 characters")
    private String status;

    @Size(max = 50, message = "Priority must be <= 50 characters")
    private String priority;

    // may be null — the task may be unassigned
    private UUID assignedTo;

    // may be null — due date is optional
    private Instant dueDate;

}
