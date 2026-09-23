package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;

import java.util.UUID;

public interface WorkspaceService {

    WorkspaceEntity createPersonalWorkspace(UUID ownerId);

    WorkspaceEntity createCompanyWorkspace(UUID ownerId, UUID companyId);

    java.util.List<WorkspaceEntity> getWorkspacesForOwner(UUID ownerId);

    /**
     * All workspaces visible to the user: own plus workspaces of every company
     * the user belongs to.
     */
    java.util.List<WorkspaceEntity> getVisibleWorkspaces(UUID ownerId, java.util.List<UUID> companyIds);

    void deleteWorkspace(UUID id);
}

