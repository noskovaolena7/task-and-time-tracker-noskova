package com.olenanoskova.task_and_time_tracker.exception;

import java.util.UUID;

public class RoleAlreadyAssignedException extends RuntimeException {
    public RoleAlreadyAssignedException(UUID userId, UUID companyId) {
        super("User " + userId + " already has a role in company " + companyId);
    }
}
