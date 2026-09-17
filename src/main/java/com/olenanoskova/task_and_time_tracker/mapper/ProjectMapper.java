package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Project;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProjectMapper {

    // DTO → Domain (Create)
    public Project toDomain(@Valid ProjectCreateRequestDto dto) {
        Project project = new Project();
        project.setCompanyId(dto.getCompanyId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        return project;
    }

    // DTO → Domain (Update)
    public void updateDomain(ProjectUpdateRequestDto dto, Project project) {
        if (dto.getName() != null) {
           project.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
           project.setDescription(dto.getDescription());
        }
        project.setUpdatedAt(Instant.now());
    }

    // Domain → Entity
    public ProjectEntity toEntity(Project project) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(project.getId());
        entity.setName(project.getName());
        entity.setDescription(project.getDescription());
        entity.setCompanyId(project.getCompanyId());
        entity.setCreatedAt(project.getCreatedAt());
        entity.setUpdatedAt(project.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public Project toDomain(ProjectEntity entity) {
        Project project = new Project();
        project.setId(entity.getId());
        project.setCompanyId(entity.getCompanyId());
        project.setName(entity.getName());
        project.setDescription(entity.getDescription());
        project.setCreatedAt(entity.getCreatedAt());
        project.setUpdatedAt(entity.getUpdatedAt());
        return project;
    }

    // Domain → Response DTO
    public ProjectResponseDto toDto(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setId(project.getId());
        dto.setCompanyId(project.getCompanyId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }
}
