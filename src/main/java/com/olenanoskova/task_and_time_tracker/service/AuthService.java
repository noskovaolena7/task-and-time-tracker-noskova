package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterCompanyRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterUserRequestDto;

public interface AuthService {

    String signUpPersonalUser(RegisterUserRequestDto request);

    String signUpCompanyUser(RegisterCompanyRequestDto request);

    String login(String email, String password);
}