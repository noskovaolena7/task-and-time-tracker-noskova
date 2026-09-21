package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class UserUpdateRequestDto {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(min = 5, max = 20, message = "Phone number must be between 5 and 20 characters")
    private String phoneNumber;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be <= 255 characters")
    private String email;

    @ToString.Exclude
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
