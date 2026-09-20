package com.olenanoskova.task_and_time_tracker.service;
import com.olenanoskova.task_and_time_tracker.service.model.Role;


public interface TokenService {

    String createToken(String id, Role role);

    boolean isValidToken(String token);

    String getUserId(String token);

    Role getRole(String token);

}
