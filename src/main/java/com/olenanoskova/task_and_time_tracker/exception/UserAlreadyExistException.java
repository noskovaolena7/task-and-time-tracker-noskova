package com.olenanoskova.task_and_time_tracker.exception;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String email) {
        super("User with email " + email + " already exists.");
    }
}
