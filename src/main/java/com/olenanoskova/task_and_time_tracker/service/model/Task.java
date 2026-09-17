package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID createdBy;
    private UUID assignedTo;
    private Instant dueDate;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;
}

