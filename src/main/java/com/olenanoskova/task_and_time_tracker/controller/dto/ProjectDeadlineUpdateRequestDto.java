package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ProjectDeadlineUpdateRequestDto {
    private Instant deadline;
    private String title;
}
