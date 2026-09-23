package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class NotificationCreateRequestDto {

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 1000, message = "Message must be <= 1000 characters")
    private String message;
}
