package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageByEmailRequestDto {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 1000, message = "Message must be <= 1000 characters")
    private String message;
}
