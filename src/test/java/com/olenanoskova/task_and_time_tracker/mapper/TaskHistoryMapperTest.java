package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskHistoryResponseDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskHistoryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskHistoryMapperTest {

    private TaskHistoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TaskHistoryMapper();
    }

    @Test
    void entityToDomain_and_domainToEntity() {
        TaskHistoryEntity entity = new TaskHistoryEntity();
        entity.setId(UUID.randomUUID());
        entity.setTaskId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setField("status");
        entity.setOldValue("OPEN");
        entity.setNewValue("IN_PROGRESS");
        entity.setChangedAt(Instant.now());

        TaskHistory domain = mapper.toDomain(entity);
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals("status", domain.getFieldName());

        TaskHistoryEntity convertedEntity = mapper.toEntity(domain);
        assertEquals(entity.getId(), convertedEntity.getId());
        assertEquals("status", convertedEntity.getField());
    }

    @Test
    void toDto_and_toDtoList() {
        TaskHistory history = new TaskHistory();
        history.setId(UUID.randomUUID());
        history.setFieldName("priority");
        history.setOldValue("LOW");
        history.setNewValue("HIGH");

        TaskHistoryResponseDto dto = mapper.toDto(history);
        assertEquals(history.getId(), dto.getId());
        assertEquals("priority", dto.getFieldChanged());
        assertEquals("LOW", dto.getOldValue());
        assertEquals("HIGH", dto.getNewValue());

        List<TaskHistoryResponseDto> list = mapper.toDtoList(List.of(history));
        assertEquals(1, list.size());
    }
}
