package com.olenanoskova.task_and_time_tracker.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password: ");
    }
}
