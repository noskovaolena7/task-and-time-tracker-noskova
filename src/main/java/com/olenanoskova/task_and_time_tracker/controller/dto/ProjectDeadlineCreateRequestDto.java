package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectDeadlineCreateRequestDto {
    private Instant deadline;
    private List<String> reminderPeriods;
    private UUID createdBy;
}
