package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity;
import com.olenanoskova.task_and_time_tracker.service.model.MemberRole;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectMember;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMemberMapperTest {
    ProjectMemberMapper mapper = new ProjectMemberMapper();

    @Test
    void smoke() { assertNotNull(mapper); }

    @Test
    void dtoToDomain_andEntityRoundtrip() {
        var dto = new ProjectMemberCreateRequestDto();
        dto.setUserId(UUID.randomUUID());
        dto.setMemberRoleDto(MemberRoleDto.MANAGER);

        var domain = mapper.toDomain(UUID.randomUUID(), dto);
        assertEquals(domain.getUserId(), dto.getUserId());
        assertEquals(MemberRole.MANAGER, domain.getMemberRole());

        domain.setCreatedAt(Instant.now());
        domain.setUpdatedAt(Instant.now());
        domain.setId(UUID.randomUUID());

        ProjectMemberEntity entity = mapper.toEntity(domain);
        assertEquals(entity.getUserId(), domain.getUserId());

        ProjectMember back = mapper.toDomain(entity);
        assertEquals(back.getId(), domain.getId());

        ProjectMemberResponseDto resp = mapper.toDto(domain);
        assertEquals(resp.getId(), domain.getId());
    }
}
