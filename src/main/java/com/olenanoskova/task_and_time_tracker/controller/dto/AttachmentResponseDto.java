package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AttachmentResponseDto {

    @NotNull(message = "Attachment ID cannot be null")
    private UUID id;

    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    @NotNull(message = "Task ID cannot be null")
    private UUID taskId;

    @NotBlank(message = "File name cannot be blank")
    @Size(max = 255, message = "File name must be <= 255 characters")
    private String fileName;

    @NotBlank(message = "File URL cannot be blank")
    @Size(max = 2048, message = "File URL must be <= 2048 characters")
    @Pattern(
            regexp = "^(https?://).+$",
            message = "File URL must be a valid HTTP/HTTPS link"
    )
    private String fileUrl;

    @NotNull(message = "Uploader ID cannot be null")
    private UUID uploadedBy;

    @NotNull(message = "Upload timestamp cannot be null")
    private Instant uploadedAt;

    @NotNull(message = "Update timestamp cannot be null")
    private Instant updatedAt;
}