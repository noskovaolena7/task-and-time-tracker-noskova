package com.olenanoskova.task_and_time_tracker.controller.dto;

import java.time.Instant;
import java.util.UUID;

public class TaskReminderCreateRequestDto {

    private Instant remindAt;
    private UUID createdBy;
    private String message;

    public Instant getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(Instant remindAt) {
        this.remindAt = remindAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
