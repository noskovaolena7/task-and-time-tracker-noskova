package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AttachmentResponseDto {

    private UUID id;
    private UUID projectId;
    private UUID taskId;
    private String fileName;
    private String fileUrl;
    private UUID uploadedBy;
    private Instant uploadedAt;
    private Instant updatedAt;

}