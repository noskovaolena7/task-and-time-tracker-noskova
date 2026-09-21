package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
public class LoginRequestDto {

     @NotBlank(message = "Email cannot be blank")
     @Email(message = "Email must be valid")
     @Size(max = 255, message = "Email must be <= 255 characters")
     private String email;

     @NotBlank(message = "Password cannot be blank")
     @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
     private String password;
}