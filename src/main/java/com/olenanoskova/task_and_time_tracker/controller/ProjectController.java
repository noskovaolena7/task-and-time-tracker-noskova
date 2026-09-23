package com.olenanoskova.task_and_time_tracker.controller;


import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.ProjectService;
import com.olenanoskova.task_and_time_tracker.service.model.Project;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("@securityService.canCreateProject(#request.companyId)")
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody ProjectCreateRequestDto request) {

        Project project = projectMapper.toDomain(request);
        Project createdProject = projectService.createProject(project);
        ProjectResponseDto response = projectMapper.toDto(createdProject);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(name = "company_id", required = false) UUID companyId) {

        UUID currentUserId = securityService.getCurrentUserId();
        List<UUID> ownCompanyIds = securityService.getCurrentUserCompanyIds();

        List<Project> projects;
        if (ownCompanyIds.isEmpty()) {
            // Personal workspace: only own projects without a company.
            projects = projectService.getPersonalProjects(currentUserId, page, size);
        } else {
            if (companyId != null && !ownCompanyIds.contains(companyId)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Cannot list projects of another company");
            }
            UUID scope = companyId != null ? companyId : null;
            projects = new java.util.ArrayList<>();
            if (scope != null) {
                projects.addAll(projectService.getProjects(page, size, scope));
            } else {
                // All projects of all own companies.
                for (UUID ownCompany : ownCompanyIds) {
                    projects.addAll(projectService.getProjects(null, null, ownCompany));
                }
                if (page != null && size != null) {
                    int from = Math.min(page * size, projects.size());
                    int to = Math.min(from + size, projects.size());
                    projects = projects.subList(from, to);
                }
            }
            // Personal projects remain visible alongside company ones.
            projects.addAll(projectService.getPersonalProjects(currentUserId, null, null));
        }
        List<ProjectResponseDto> responseList = projects.stream()
                .map(projectMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessProject(#id)")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable UUID id) {

        Project project = projectService.getProjectById(id);
        ProjectResponseDto response = projectMapper.toDto(project);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canUpdateProject(#id)")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectUpdateRequestDto request) {

        Project project = projectService.getProjectById(id);
        projectMapper.updateDomain(request, project);
        Project updatedProject = projectService.updateProject(id, project);
        ProjectResponseDto response = projectMapper.toDto(updatedProject);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canDeleteProject(#id)")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}
