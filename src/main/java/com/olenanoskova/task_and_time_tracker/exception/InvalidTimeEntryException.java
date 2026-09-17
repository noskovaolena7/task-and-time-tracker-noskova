package com.olenanoskova.task_and_time_tracker.exception;

public class InvalidTimeEntryException extends RuntimeException {
    public InvalidTimeEntryException(String message) {
        super("Invalid time entry: " + message);
    }
}

