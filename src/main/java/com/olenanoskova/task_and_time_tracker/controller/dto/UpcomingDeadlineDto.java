package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UpcomingDeadlineDto {

    private UUID id;

    private UUID projectId;

    private String projectName;

    private String title;

    private Instant deadline;

    private boolean overdue;
}
