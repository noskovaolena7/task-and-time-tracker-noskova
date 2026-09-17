package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class ProjectMemberAlreadyExistsException extends RuntimeException {
    public ProjectMemberAlreadyExistsException(UUID userId, UUID projectId) {
        super("User " + userId + " is already a member of project " + projectId);
    }
}

