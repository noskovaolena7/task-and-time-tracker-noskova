package com.olenanoskova.task_and_time_tracker.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    @NotNull(message = "User ID cannot be null")
    private UUID id;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name must be <= 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name must be <= 50 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be <= 255 characters")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 5, max = 20, message = "Phone number must be between 5 and 20 characters")
    private String phoneNumber;

    @NotNull(message = "Member role cannot be null")
    @Valid
    private MemberRoleDto memberRole;
}
