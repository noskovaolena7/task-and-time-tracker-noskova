package com.olenanoskova.task_and_time_tracker.exception;


import java.util.UUID;

public class UserCompanyRoleNotFoundException extends RuntimeException {
    public UserCompanyRoleNotFoundException(UUID id) {
        super("UserCompanyRole not found: " + id);
    }
}

