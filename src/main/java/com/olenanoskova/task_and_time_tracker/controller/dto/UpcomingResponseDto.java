package com.olenanoskova.task_and_time_tracker.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpcomingResponseDto {

    private List<UpcomingDeadlineDto> deadlines;

    private List<UpcomingReminderDto> reminders;
}
