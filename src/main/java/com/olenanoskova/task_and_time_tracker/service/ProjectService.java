package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(Project project);

    List<Project> getProjects(Integer page, Integer size, UUID companyId);

    Project getProjectById(UUID id);

    Project updateProject(UUID id, Project project);

    void deleteProject(UUID id);
}
