package com.olenanoskova.task_and_time_tracker.controller.dto;

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

    // projectId може бути null, якщо нотифікація не прив'язана до проєкту
    private UUID projectId;

    // taskId може бути null, якщо нотифікація не прив'язана до задачі
    private UUID taskId;

    @NotBlank(message = "Notification type cannot be blank")
    @Size(max = 100, message = "Notification type must be <= 100 characters")
    private String type;

    @NotBlank(message = "Notification message cannot be blank")
    @Size(max = 1000, message = "Notification message must be <= 1000 characters")
    private String message;

    @NotNull(message = "Read status cannot be null")
    private Boolean isRead;

    // scheduledAt може бути null, якщо нотифікація не запланована
    private Instant scheduledAt;

    // sentAt може бути null, якщо нотифікація ще не відправлена
    private Instant sentAt;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // updatedAt може бути null, якщо нотифікацію ще не редагували
    private Instant updatedAt;


}
