package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CompanyResponseDto {

    @NotNull(message = "Company ID cannot be null")
    private UUID id;

    @NotBlank(message = "Company name cannot be blank")
    @Size(max = 100, message = "Company name must be <= 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be <= 500 characters")
    private String description;

    private UUID ownerId;

    private UUID workspaceId;

    @NotNull(message = "Creation timestamp cannot be null")
    private Instant createdAt;

    // updatedAt may be null if the company has not been edited yet
    private Instant updatedAt;
}
