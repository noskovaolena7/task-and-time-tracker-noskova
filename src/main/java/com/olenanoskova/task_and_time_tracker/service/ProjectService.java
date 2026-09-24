package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(Project project);

    List<Project> getProjects(Integer page, Integer size, UUID companyId);

    /**
     * Personal projects of a user: company_id IS NULL and created_by = userId.
     */
    List<Project> getPersonalProjects(UUID userId, Integer page, Integer size);

    /**
     * Projects where the user is a member (personal or company ones).
     * Membership grants access even without authorship or company role.
     */
    List<Project> getMemberProjects(UUID userId);

    Project getProjectById(UUID id);

    Project updateProject(UUID id, Project project);

    void deleteProject(UUID id);
}
