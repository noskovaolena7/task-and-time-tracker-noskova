package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {

    @NotBlank(message = "Token cannot be blank")
    private String token;
}
