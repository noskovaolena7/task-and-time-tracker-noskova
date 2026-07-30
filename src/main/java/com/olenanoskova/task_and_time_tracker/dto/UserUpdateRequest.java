package com.olenanoskova.task_and_time_tracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
}
