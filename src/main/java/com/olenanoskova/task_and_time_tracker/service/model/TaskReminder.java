package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskReminder {
    private UUID id;
    private UUID taskId;
    private UUID createdBy;
    private Instant remindAt;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;
}

