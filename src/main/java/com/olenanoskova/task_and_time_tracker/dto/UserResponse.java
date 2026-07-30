package com.olenanoskova.task_and_time_tracker.dto;


import com.olenanoskova.task_and_time_tracker.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;

    public UserResponse(UUID id, String fullName, String email,
                        String phoneNumber, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
