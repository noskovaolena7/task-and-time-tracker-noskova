package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class ProjectMemberNotFoundException extends RuntimeException {
    public ProjectMemberNotFoundException(UUID id) {
        super("Project member not found: " + id);
    }
}
