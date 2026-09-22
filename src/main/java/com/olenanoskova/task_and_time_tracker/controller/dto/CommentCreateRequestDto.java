package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.UUID;
@Data
public class CommentCreateRequestDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Comment text cannot be blank")
    @Size(min = 1, max = 1000, message = "Comment text must be between 1 and 1000 characters")
    private String text;


}
