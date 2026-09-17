package com.olenanoskova.task_and_time_tracker.exception;

public class InvalidReminderException extends RuntimeException {
    public InvalidReminderException(String message) {
        super("Invalid reminder: " + message);
    }
}

