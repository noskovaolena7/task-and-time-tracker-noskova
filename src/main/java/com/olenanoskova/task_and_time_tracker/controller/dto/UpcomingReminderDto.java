package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UpcomingReminderDto {

    private UUID id;

    private UUID taskId;

    private String taskTitle;

    private UUID projectId;

    private String projectName;

    private Instant remindAt;

    private String message;

    private boolean overdue;
}
