package com.olenanoskova.task_and_time_tracker.exception;

public class InvalidTaskStatusException extends RuntimeException {
    public InvalidTaskStatusException(String status) {
        super("Invalid task status: " + status);
    }
}
