package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskReminderCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskReminderResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskReminderEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskReminderMapperTest {

    private TaskReminderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TaskReminderMapper();
    }

    @Test
    void toDomain_fromCreateDto() {
        UUID taskId = UUID.randomUUID();
        TaskReminderCreateRequestDto dto = new TaskReminderCreateRequestDto();
        dto.setCreatedBy(UUID.randomUUID());
        dto.setRemindAt(Instant.now().plusSeconds(3600));
        dto.setMessage("Check status");

        TaskReminder domain = mapper.toDomain(dto, taskId);
        assertNotNull(domain);
        assertEquals(taskId, domain.getTaskId());
        assertEquals("Check status", domain.getMessage());
        assertEquals(dto.getRemindAt(), domain.getRemindAt());
    }

    @Test
    void domainToEntity_and_back() {
        TaskReminder domain = new TaskReminder();
        domain.setId(UUID.randomUUID());
        domain.setTaskId(UUID.randomUUID());
        domain.setMessage("Meeting reminder");
        domain.setRemindAt(Instant.now());

        TaskReminderEntity entity = mapper.toEntity(domain);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getMessage(), entity.getMessage());

        TaskReminder mappedBack = mapper.toDomain(entity);
        assertEquals(domain.getId(), mappedBack.getId());
        assertEquals(domain.getMessage(), mappedBack.getMessage());
    }

    @Test
    void toDto() {
        TaskReminder domain = new TaskReminder();
        domain.setId(UUID.randomUUID());
        domain.setTaskId(UUID.randomUUID());
        domain.setRemindAt(Instant.now());

        TaskReminderResponseDto dto = mapper.toDto(domain);
        assertEquals(domain.getId(), dto.getId());
        assertEquals(domain.getTaskId(), dto.getTaskId());
    }
}
