package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.RoleDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserCompanyRoleMapper {

    // DTO → Domain (Create)
    public UserCompanyRole toDomain(@Valid UserCompanyRoleCreateRequestDto dto) {
        UserCompanyRole role = new UserCompanyRole();
        role.setUserId(dto.getUserId());
        role.setRole(mapToMemberRole(dto.getRole()));
        role.setCreatedAt(Instant.now());
        role.setUpdatedAt(Instant.now());
        return role;
    }

    /**
     * Maps the 6-value API {@link RoleDto} onto the 4-value {@link MemberRole}:
     * PERSONAL_USER/COMPANY_USER have no company-level equivalent and map to USER.
     */
    static MemberRole mapToMemberRole(RoleDto dto) {
        if (dto == null) {
            return null;
        }
        return switch (dto) {
            case USER, PERSONAL_USER, COMPANY_USER -> MemberRole.USER;
            case MANAGER -> MemberRole.MANAGER;
            case ADMIN -> MemberRole.ADMIN;
            case OWNER -> MemberRole.OWNER;
        };
    }

    // Domain → Entity
    public UserCompanyRoleEntity toEntity(UserCompanyRole role) {
        UserCompanyRoleEntity entity = new UserCompanyRoleEntity();
        entity.setId(role.getId());
        entity.setUserId(role.getUserId());
        entity.setCompanyId(role.getCompanyId());
        entity.setRole(MemberRoleEntity.valueOf(role.getRole().name()));
        entity.setInvitedBy(role.getInvitedBy());
        entity.setCreatedAt(role.getCreatedAt());
        entity.setUpdatedAt(role.getUpdatedAt());
        return entity;
    }

    // Entity → Domain
    public UserCompanyRole toDomain(UserCompanyRoleEntity entity) {
        UserCompanyRole role = new UserCompanyRole();
        role.setId(entity.getId());
        role.setUserId(entity.getUserId());
        role.setCompanyId(entity.getCompanyId());
        role.setRole(MemberRole.valueOf(entity.getRole().name()));
        role.setInvitedBy(entity.getInvitedBy());
        role.setCreatedAt(entity.getCreatedAt());
        role.setUpdatedAt(entity.getUpdatedAt());
        return role;
    }

    // Domain → Response DTO
    public UserCompanyRoleResponseDto toDto(UserCompanyRole role) {
        UserCompanyRoleResponseDto dto = new UserCompanyRoleResponseDto();
        dto.setId(role.getId());
        dto.setUserId(role.getUserId());
        dto.setCompanyId(role.getCompanyId());
        dto.setRole(RoleDto.valueOf(role.getRole().name()));
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }

}
