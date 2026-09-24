package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;

import java.util.List;
import java.util.UUID;

public interface ProjectDeadlineService {

    List<ProjectDeadline> getAllDeadlines(UUID projectId);

    ProjectDeadline createDeadline(UUID projectId, ProjectDeadline deadline);

    ProjectDeadline getDeadlineById(UUID id);

    ProjectDeadline updateDeadline(UUID projectId, UUID deadlineId, ProjectDeadlineUpdateRequestDto request);

    void deleteDeadline(UUID projectId, UUID deadlineId);
}
