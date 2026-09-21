package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {

    @NotBlank(message = "Error message cannot be blank")
    @Size(max = 500, message = "Error message must be <= 500 characters")
    private String message;

    @NotBlank(message = "Error code cannot be blank")
    @Size(max = 100, message = "Error code must be <= 100 characters")
    private String code;
}


