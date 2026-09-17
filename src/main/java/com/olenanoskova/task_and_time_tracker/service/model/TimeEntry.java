package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeEntry {
    private UUID id;
    private UUID taskId;
    private UUID userId;
    private Instant startTime;
    private Instant endTime;
    private Long durationSeconds;
    private Instant createdAt;
    private Instant updatedAt;
}
