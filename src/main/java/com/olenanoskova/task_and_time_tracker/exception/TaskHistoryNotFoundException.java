package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class TaskHistoryNotFoundException extends RuntimeException {
    public TaskHistoryNotFoundException(UUID id) {
        super("Task history record not found: " + id);
    }
}

