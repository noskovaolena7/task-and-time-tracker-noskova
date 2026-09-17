package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private NotificationStatus status;
    private String type;
    private String message;
    private boolean isRead;
    private Instant scheduledAt;
    private Instant sentAt;
    private Instant createdAt;
    private Instant updatedAt;
}


