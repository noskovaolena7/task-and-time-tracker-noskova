package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("User with id " + userId + " was not found.");
    }
}