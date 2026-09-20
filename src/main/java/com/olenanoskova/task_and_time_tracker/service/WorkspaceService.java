package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;

import java.util.UUID;

public interface WorkspaceService {

    WorkspaceEntity createPersonalWorkspace(UUID ownerId);

    WorkspaceEntity createCompanyWorkspace(UUID ownerId, UUID companyId);
}

