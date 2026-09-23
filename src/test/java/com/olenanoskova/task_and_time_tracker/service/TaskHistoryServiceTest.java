package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.TaskHistoryNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TaskHistoryMapper;
import com.olenanoskova.task_and_time_tracker.repository.TaskHistoryRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskHistoryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskHistoryServiceTest {

    @Mock
    private TaskHistoryRepository taskHistoryRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskHistoryMapper taskHistoryMapper;

    @InjectMocks
    private TaskHistoryServiceImpl taskHistoryService;

    @Test
    void getTaskHistory_taskNotFound_throws() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskHistoryService.getTaskHistory(taskId));
    }

    @Test
    void getTaskHistory_success() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);

        TaskHistoryEntity entity = new TaskHistoryEntity();
        TaskHistory domain = new TaskHistory();

        when(taskHistoryRepository.findByTaskId(taskId)).thenReturn(List.of(entity));
        when(taskHistoryMapper.toDomain(entity)).thenReturn(domain);

        List<TaskHistory> list = taskHistoryService.getTaskHistory(taskId);
        assertEquals(1, list.size());
    }

    @Test
    void getHistoryRecordById_found() {
        UUID id = UUID.randomUUID();
        TaskHistoryEntity entity = new TaskHistoryEntity();
        TaskHistory domain = new TaskHistory();

        when(taskHistoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(taskHistoryMapper.toDomain(entity)).thenReturn(domain);

        TaskHistory result = taskHistoryService.getHistoryRecordById(id);
        assertNotNull(result);
    }

    @Test
    void getHistoryRecordById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(taskHistoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskHistoryNotFoundException.class, () -> taskHistoryService.getHistoryRecordById(id));
    }
}
