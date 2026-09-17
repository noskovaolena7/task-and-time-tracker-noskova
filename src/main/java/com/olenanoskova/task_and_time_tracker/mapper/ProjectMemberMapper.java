package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectMember;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProjectMemberMapper {

    // DTO → Domain (Create)
    public ProjectMember toDomain(@Valid ProjectMemberCreateRequestDto dto) {
        ProjectMember member = new ProjectMember();
        member.setUserId(dto.getUserId());
        member.setMemberRole(MemberRole.valueOf(dto.getMemberRoleDto().name()));
        member.setCreatedAt(Instant.now());
        member.setUpdatedAt(Instant.now());
        return member;
    }

    // DTO → Domain (Update)
    public void updateDomain(ProjectMemberCreateRequestDto dto, ProjectMember member) {
        member.setMemberRole(MemberRole.valueOf(dto.getMemberRoleDto().name()));
        member.setUpdatedAt(Instant.now());
    }

    // DTO + projectId → Domain
    public ProjectMember toDomain(UUID projectId, @Valid ProjectMemberCreateRequestDto dto) {
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(dto.getUserId());
        member.setMemberRole(MemberRole.valueOf(dto.getMemberRoleDto().name()));
        member.setCreatedAt(Instant.now());
        member.setUpdatedAt(Instant.now());
        return member;
    }

    // Domain → Entity
    public ProjectMemberEntity toEntity(ProjectMember member) {
        ProjectMemberEntity entity = new ProjectMemberEntity();
        entity.setId(member.getId());
        entity.setUserId(member.getUserId());
        entity.setProjectId(member.getProjectId());
        entity.setMemberRole(MemberRoleEntity.valueOf(member.getMemberRole().name()));
        entity.setCreatedAt(member.getCreatedAt());
        entity.setUpdatedAt(member.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public ProjectMember toDomain(ProjectMemberEntity entity) {
        ProjectMember member = new ProjectMember();
        member.setId(entity.getId());
        member.setUserId(entity.getUserId());
        member.setProjectId(entity.getProjectId());
        member.setMemberRole(MemberRole.valueOf(entity.getMemberRole().name()));
        member.setCreatedAt(entity.getCreatedAt());
        member.setUpdatedAt(entity.getUpdatedAt());
        return member;
    }

    // Domain → Response DTO
    public ProjectMemberResponseDto toDto(ProjectMember member) {
        ProjectMemberResponseDto dto = new ProjectMemberResponseDto();
        dto.setId(member.getId());
        dto.setUserId(member.getUserId());
        dto.setProjectId(member.getProjectId());
        dto.setMemberRoleDto(MemberRoleDto.valueOf(member.getMemberRole().name()));
        dto.setCreatedAt(member.getCreatedAt());
        dto.setUpdatedAt(member.getUpdatedAt());
        return dto;
    }
}
