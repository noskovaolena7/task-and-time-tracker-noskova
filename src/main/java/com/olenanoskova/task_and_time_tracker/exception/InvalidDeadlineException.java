package com.olenanoskova.task_and_time_tracker.exception;

public class InvalidDeadlineException extends RuntimeException {
    public InvalidDeadlineException(String message) {
        super("Invalid deadline: " + message);
    }
}

