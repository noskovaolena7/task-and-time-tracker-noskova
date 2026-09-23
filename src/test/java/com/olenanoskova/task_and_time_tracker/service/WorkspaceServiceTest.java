package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.WorkspaceRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceTypeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private WorkspaceRepository repo;

    @InjectMocks
    private WorkspaceServiceImpl service;

    @Test
    void createPersonalWorkspace_success() {
        UUID ownerId = UUID.randomUUID();
        WorkspaceEntity saved = new WorkspaceEntity();
        saved.setId(UUID.randomUUID());
        saved.setOwnerId(ownerId);
        saved.setType(WorkspaceTypeEntity.PERSONAL);
        saved.setName("Personal Workspace");

        when(repo.save(any(WorkspaceEntity.class))).thenReturn(saved);

        WorkspaceEntity result = service.createPersonalWorkspace(ownerId);
        assertNotNull(result);
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(WorkspaceTypeEntity.PERSONAL, result.getType());
    }

    @Test
    void createCompanyWorkspace_success() {
        UUID ownerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        WorkspaceEntity saved = new WorkspaceEntity();
        saved.setId(UUID.randomUUID());
        saved.setOwnerId(ownerId);
        saved.setCompanyId(companyId);
        saved.setType(WorkspaceTypeEntity.COMPANY);
        saved.setName("Company Workspace");

        when(repo.save(any(WorkspaceEntity.class))).thenReturn(saved);

        WorkspaceEntity result = service.createCompanyWorkspace(ownerId, companyId);
        assertNotNull(result);
        assertEquals(companyId, result.getCompanyId());
        assertEquals(WorkspaceTypeEntity.COMPANY, result.getType());
    }
}
