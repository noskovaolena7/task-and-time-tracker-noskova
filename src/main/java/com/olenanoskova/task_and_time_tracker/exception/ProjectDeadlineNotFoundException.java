package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class ProjectDeadlineNotFoundException extends RuntimeException {
    public ProjectDeadlineNotFoundException(UUID id) {
        super("Project deadline not found: " + id);
    }
}

