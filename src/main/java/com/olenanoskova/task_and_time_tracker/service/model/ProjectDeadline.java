package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDeadline {
    private UUID id;
    private UUID projectId;
    private Instant deadline;
    private List<String> reminderPeriods;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;

}

