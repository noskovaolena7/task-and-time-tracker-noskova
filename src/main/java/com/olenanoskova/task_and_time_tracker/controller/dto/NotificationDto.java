package com.olenanoskova.task_and_time_tracker.controller.dto;

import java.time.Instant;
import java.util.UUID;

public class NotificationDto {

    private UUID id;
    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private String type;
    private String message;
    private boolean isRead;
    private Instant scheduledAt;
    private Instant sentAt;
    private Instant createdAt;
}
