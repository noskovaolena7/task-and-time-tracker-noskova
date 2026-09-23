package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class InviteCreateRequestDto {

    @NotNull(message = "Company ID is required")
    private UUID companyId;
}
