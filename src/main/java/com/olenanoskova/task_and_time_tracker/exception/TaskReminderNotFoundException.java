package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class TaskReminderNotFoundException extends RuntimeException {
    public TaskReminderNotFoundException(UUID id) {
        super("Task reminder not found: " + id);
    }
}

