package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

@Data
public class RegisterCompanyRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String companyName;
    private String companyDescription;
}
