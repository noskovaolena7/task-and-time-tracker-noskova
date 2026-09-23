package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.RoleDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserCompanyRoleResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.MemberRoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserCompanyRoleEntity;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import com.olenanoskova.task_and_time_tracker.service.model.UserCompanyRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserCompanyRoleMapperTest {

    private UserCompanyRoleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserCompanyRoleMapper();
    }

    @Test
    void toDomain_fromCreateDto() {
        UserCompanyRoleCreateRequestDto dto = new UserCompanyRoleCreateRequestDto();
        dto.setUserId(UUID.randomUUID());
        dto.setRole(RoleDto.MANAGER);

        UserCompanyRole role = mapper.toDomain(dto);
        assertNotNull(role);
        assertEquals(dto.getUserId(), role.getUserId());
        assertEquals(MemberRole.MANAGER, role.getRole());
    }

    @Test
    void domainToEntity_and_back() {
        UserCompanyRole role = new UserCompanyRole();
        role.setId(UUID.randomUUID());
        role.setUserId(UUID.randomUUID());
        role.setCompanyId(UUID.randomUUID());
        role.setRole(MemberRole.OWNER);

        UserCompanyRoleEntity entity = mapper.toEntity(role);
        assertEquals(role.getId(), entity.getId());
        assertEquals(MemberRoleEntity.OWNER, entity.getRole());

        UserCompanyRole mappedBack = mapper.toDomain(entity);
        assertEquals(role.getId(), mappedBack.getId());
        assertEquals(MemberRole.OWNER, mappedBack.getRole());
    }

    @Test
    void toDto() {
        UserCompanyRole role = new UserCompanyRole();
        role.setId(UUID.randomUUID());
        role.setUserId(UUID.randomUUID());
        role.setCompanyId(UUID.randomUUID());
        role.setRole(MemberRole.USER);

        UserCompanyRoleResponseDto dto = mapper.toDto(role);
        assertEquals(role.getId(), dto.getId());
        assertEquals(RoleDto.USER, dto.getRole());
    }
}
