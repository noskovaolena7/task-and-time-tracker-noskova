package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;
@Data
public class ProjectResponseDto {

        @NotNull(message = "Project ID cannot be null")
        private UUID id;

        @NotNull(message = "Company ID cannot be null")
        private UUID companyId;

        @NotBlank(message = "Project name cannot be blank")
        @Size(max = 100, message = "Project name must be <= 100 characters")
        private String name;

        @Size(max = 1000, message = "Description must be <= 1000 characters")
        private String description;

        @NotNull(message = "Creator ID cannot be null")
        private UUID createdBy;

        @NotNull(message = "Creation timestamp cannot be null")
        private Instant createdAt;

        private Instant updatedAt;


}
