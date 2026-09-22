package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentCreateRequestDto {

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must be <= 255 characters")
    private String fileName;

    @NotBlank(message = "File URL is required") // ← додано
    @Size(max = 2048, message = "File URL must be <= 2048 characters") // ← додано
    @Pattern(
            regexp = "^(https?://).+$",
            message = "File URL must be a valid HTTP/HTTPS link")
    private String fileUrl;

    @NotNull(message = "Uploader ID is required")
    private UUID uploadedBy;

}
