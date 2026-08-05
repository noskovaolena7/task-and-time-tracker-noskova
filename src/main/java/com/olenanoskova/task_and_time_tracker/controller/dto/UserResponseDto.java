package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;

}
