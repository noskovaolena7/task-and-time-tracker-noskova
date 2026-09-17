package com.olenanoskova.task_and_time_tracker.controller.dto;

import java.time.Instant;
import java.util.UUID;

public class TimeEntryCreateRequestDto {

    private UUID taskId;
    private UUID userId;
    private Instant startTime;
    private Instant endTime;

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
