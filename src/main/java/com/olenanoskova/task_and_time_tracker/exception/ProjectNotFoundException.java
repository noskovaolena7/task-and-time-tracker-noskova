package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(UUID id) {
        super("Project not found: " + id);
    }
}
