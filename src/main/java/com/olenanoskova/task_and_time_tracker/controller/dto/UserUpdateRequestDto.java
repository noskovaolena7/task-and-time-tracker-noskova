package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;
}
