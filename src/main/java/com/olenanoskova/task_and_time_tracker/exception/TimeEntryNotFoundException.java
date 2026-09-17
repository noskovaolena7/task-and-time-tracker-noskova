package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class TimeEntryNotFoundException extends RuntimeException {
    public TimeEntryNotFoundException(UUID id) {
        super("TimeEntry not found: " + id);
    }
}

