package com.olenanoskova.task_and_time_tracker.controller.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProjectUpdateRequestDto {

    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description must be <= 1000 characters")
    private String description;


}
