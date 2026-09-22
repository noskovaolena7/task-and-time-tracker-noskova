package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskCreateRequestDto {

        @NotNull(message = "Project ID is required")
        private UUID projectId;

        @NotBlank(message = "Task title cannot be blank")
        @Size(min = 2, max = 200, message = "Task title must be between 2 and 200 characters")
        private String title;

        @Size(max = 2000, message = "Description must be <= 2000 characters")
        private String description;

        @NotBlank(message = "Status cannot be blank")
        @Size(max = 50, message = "Status must be <= 50 characters")
        private String status;

        @NotBlank(message = "Priority cannot be blank")
        @Size(max = 50, message = "Priority must be <= 50 characters")
        private String priority;

        @NotNull(message = "Creator ID is required")
        private UUID createdBy;

        // може бути null — задача може бути не призначена
        private UUID assignedTo;

        // може бути null — дедлайн не обов’язковий
        private Instant dueDate;
   
}
