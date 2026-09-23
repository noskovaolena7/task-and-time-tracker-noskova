package com.olenanoskova.task_and_time_tracker.controller.dto;

import com.olenanoskova.task_and_time_tracker.service.model.NotificationStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;
@Data
public class NotificationResponseDto {

    @NotNull(message = "Notification ID cannot be null")
    private UUID id;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    // sender of a team message; null for system notifications
    private UUID senderId;

    private String senderName;

    private String senderEmail;

    // projectId may be null if the notification is not linked to a project
    private UUID projectId;

    // taskId may be null if the notification is not linked to a task
    private UUID taskId;

    // status may be null for notifications created before migration 010
    private NotificationStatus status;

    @NotBlank(message = "Notification type cannot be blank")
    @Size(max = 100, message = "Notification type must be <= 100 characters")
    private String type;

    @NotBlank(message = "Notification message cannot be blank")
    @Size(max = 1000, message = "Notification message must be <= 1000 characters")
    private String message;

    @NotNull(message = "Read status cannot be null")
    private Boolean isRead;

    // scheduledAt may be null if the notification is not scheduled
    private Instant scheduledAt;

    // sentAt may be null if the notification has not been sent yet
    private Instant sentAt;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // updatedAt may be null if the notification has not been edited yet
    private Instant updatedAt;


}
