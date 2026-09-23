package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class WorkspaceNotFoundException extends RuntimeException {
    public WorkspaceNotFoundException(UUID id) {
        super("Workspace not found: " + id);
    }
}
