package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;


@Data
public class NotificationDto {

    @NotNull(message = "Notification ID cannot be null")
    private UUID id;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    // projectId may be null if the notification is not linked to a project
    private UUID projectId;

    // taskId may be null if the notification is not linked to a task
    private UUID taskId;

    @NotBlank(message = "Notification type cannot be blank")
    @Size(max = 100, message = "Notification type must be <= 100 characters")
    private String type;

    @NotBlank(message = "Notification message cannot be blank")
    @Size(max = 1000, message = "Notification message must be <= 1000 characters")
    private String message;

    private boolean isRead;

    // scheduledAt may be null if the notification is not scheduled
    private Instant scheduledAt;

    // sentAt may be null if the notification has not been sent yet
    private Instant sentAt;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;
}
