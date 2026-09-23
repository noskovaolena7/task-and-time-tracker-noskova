package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.WorkspaceResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WorkspaceResponseDto>> getMyWorkspaces() {

        UUID ownerId = securityService.getCurrentUserId();
        List<UUID> companyIds = securityService.getCurrentUserCompanyIds();
        List<WorkspaceResponseDto> responseList = workspaceService
                .getVisibleWorkspaces(ownerId, companyIds).stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canManageWorkspace(#id)")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable UUID id) {

        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }

    private WorkspaceResponseDto toDto(WorkspaceEntity entity) {
        WorkspaceResponseDto dto = new WorkspaceResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setOwnerId(entity.getOwnerId());
        dto.setCompanyId(entity.getCompanyId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
