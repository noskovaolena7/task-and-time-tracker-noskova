package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectDeadlineEntity;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDeadlineMapperTest {

    private ProjectDeadlineMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProjectDeadlineMapper();
    }

    @Test
    void toDomain_fromCreateDto() {
        UUID projectId = UUID.randomUUID();
        ProjectDeadlineCreateRequestDto dto = new ProjectDeadlineCreateRequestDto();
        dto.setCreatedBy(UUID.randomUUID());
        dto.setDeadline(Instant.now().plusSeconds(3600));
        dto.setReminderPeriods(List.of("1_DAY", "1_HOUR"));

        ProjectDeadline domain = mapper.toDomain(dto, projectId);
        assertNotNull(domain);
        assertEquals(projectId, domain.getProjectId());
        assertEquals(dto.getDeadline(), domain.getDeadline());
        assertEquals(2, domain.getReminderPeriods().size());
    }

    @Test
    void domainToEntity_and_back() {
        ProjectDeadline domain = new ProjectDeadline();
        domain.setId(UUID.randomUUID());
        domain.setProjectId(UUID.randomUUID());
        domain.setDeadline(Instant.now());
        domain.setReminderPeriods(List.of("1_DAY"));

        ProjectDeadlineEntity entity = mapper.toEntity(domain);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getProjectId(), entity.getProjectId());

        ProjectDeadline mappedBack = mapper.toDomain(entity);
        assertEquals(domain.getId(), mappedBack.getId());
        assertEquals(domain.getReminderPeriods(), mappedBack.getReminderPeriods());
    }

    @Test
    void toDto() {
        ProjectDeadline domain = new ProjectDeadline();
        domain.setId(UUID.randomUUID());
        domain.setProjectId(UUID.randomUUID());
        domain.setDeadline(Instant.now());

        ProjectDeadlineResponseDto dto = mapper.toDto(domain);
        assertEquals(domain.getId(), dto.getId());
        assertEquals(domain.getProjectId(), dto.getProjectId());
    }
}
