package com.olenanoskova.task_and_time_tracker.service;
import com.olenanoskova.task_and_time_tracker.service.model.Role;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {

    String createToken(String id, Role role);

    boolean isValidToken(String token);

    String getId(String token);

    Role getRole(String token);

}
