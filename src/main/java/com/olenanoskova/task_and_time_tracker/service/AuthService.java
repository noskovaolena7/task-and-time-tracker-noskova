package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.User;

public interface AuthService {

    String signUp(User user);

    String loginUser(String email, String password);

}