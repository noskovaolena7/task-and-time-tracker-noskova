package com.olenanoskova.task_and_time_tracker.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    private UUID id;

    private UUID projectId;

    private UUID taskId;

    private String fileName;

    private String fileUrl;

    private UUID uploadedBy;

    private Instant uploadedAt;

    private Instant createdAt;

    private Instant updatedAt;
}

