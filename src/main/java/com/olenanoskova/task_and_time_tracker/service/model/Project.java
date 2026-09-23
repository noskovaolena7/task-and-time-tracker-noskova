package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private UUID id;
    private String name;
    private String description;
    private UUID companyId;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}

