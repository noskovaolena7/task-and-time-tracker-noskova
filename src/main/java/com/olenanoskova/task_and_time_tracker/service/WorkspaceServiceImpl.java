package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.WorkspaceNotFoundException;
import com.olenanoskova.task_and_time_tracker.repository.WorkspaceRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Override
    public WorkspaceEntity createPersonalWorkspace(UUID ownerId) {
        WorkspaceEntity ws = new WorkspaceEntity();
        ws.setName("Personal Workspace");
        ws.setType(WorkspaceTypeEntity.PERSONAL);
        ws.setOwnerId(ownerId);
        ws.setCreatedAt(Instant.now());
        ws.setUpdatedAt(Instant.now());
        return workspaceRepository.save(ws);
    }

    @Override
    public WorkspaceEntity createCompanyWorkspace(UUID ownerId, UUID companyId) {
        WorkspaceEntity ws = new WorkspaceEntity();
        ws.setName("Company Workspace");
        ws.setType(WorkspaceTypeEntity.COMPANY);
        ws.setOwnerId(ownerId);
        ws.setCompanyId(companyId);
        ws.setCreatedAt(Instant.now());
        ws.setUpdatedAt(Instant.now());
        return workspaceRepository.save(ws);
    }

    @Override
    public List<WorkspaceEntity> getWorkspacesForOwner(UUID ownerId) {
        return workspaceRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<WorkspaceEntity> getVisibleWorkspaces(UUID ownerId, List<UUID> companyIds) {
        java.util.LinkedHashMap<UUID, WorkspaceEntity> visible = new java.util.LinkedHashMap<>();
        for (WorkspaceEntity ws : workspaceRepository.findByOwnerId(ownerId)) {
            visible.put(ws.getId(), ws);
        }
        if (companyIds != null) {
            for (UUID companyId : companyIds) {
                for (WorkspaceEntity ws : workspaceRepository.findByCompanyId(companyId)) {
                    visible.putIfAbsent(ws.getId(), ws);
                }
            }
        }
        return List.copyOf(visible.values());
    }

    @Override
    public void deleteWorkspace(UUID id) {
        if (!workspaceRepository.existsById(id)) {
            throw new WorkspaceNotFoundException(id);
        }
        workspaceRepository.deleteById(id);
    }
}
