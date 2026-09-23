package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskPriorityEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskStatusEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Task;
import com.olenanoskova.task_and_time_tracker.service.model.TaskPriority;
import com.olenanoskova.task_and_time_tracker.service.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private TaskMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TaskMapper();
    }

    @Test
    void toDomain_fromCreateDto() {
        TaskCreateRequestDto dto = new TaskCreateRequestDto();
        dto.setProjectId(UUID.randomUUID());
        dto.setTitle("Implement feature");
        dto.setDescription("Full description");
        dto.setStatus("OPEN");
        dto.setPriority("HIGH");
        dto.setCreatedBy(UUID.randomUUID());

        Task task = mapper.toDomain(dto);
        assertNotNull(task);
        assertEquals("Implement feature", task.getTitle());
        assertEquals(TaskStatus.OPEN, task.getStatus());
        assertEquals(TaskPriority.HIGH, task.getPriority());
    }

    @Test
    void updateDomain() {
        Task task = new Task();
        task.setTitle("Old Title");
        task.setStatus(TaskStatus.OPEN);

        TaskUpdateRequestDto update = new TaskUpdateRequestDto();
        update.setTitle("New Title");
        update.setStatus("IN_PROGRESS");

        mapper.updateDomain(update, task);
        assertEquals("New Title", task.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void domainToEntity_and_back() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Test task");
        task.setStatus(TaskStatus.DONE);
        task.setPriority(TaskPriority.LOW);

        TaskEntity entity = mapper.toEntity(task);
        assertEquals(TaskStatusEntity.DONE, entity.getStatus());
        assertEquals(TaskPriorityEntity.LOW, entity.getPriority());

        Task mappedBack = mapper.toDomain(entity);
        assertEquals(task.getTitle(), mappedBack.getTitle());
        assertEquals(TaskStatus.DONE, mappedBack.getStatus());
    }

    @Test
    void toDto() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Test task");
        task.setStatus(TaskStatus.OPEN);
        task.setPriority(TaskPriority.MEDIUM);

        TaskResponseDto dto = mapper.toDto(task);
        assertEquals("Test task", dto.getTitle());
        assertEquals("OPEN", dto.getStatus());
        assertEquals("MEDIUM", dto.getPriority());
    }
}
