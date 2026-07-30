package com.olenanoskova.task_and_time_tracker.dto;


import com.olenanoskova.task_and_time_tracker.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;

    public UserResponse(String id, String fullName, String email,
                        String phoneNumber, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = String.valueOf(role);
    }
}
