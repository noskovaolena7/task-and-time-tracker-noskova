package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserCreateRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private UUID companyId;
}
