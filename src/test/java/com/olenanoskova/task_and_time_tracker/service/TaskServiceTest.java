package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.mapper.TaskMapper;
import com.olenanoskova.task_and_time_tracker.service.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.ProjectRepository projectRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.UserRepository userRepository;

    @Mock
    TaskMapper taskMapper;

    @InjectMocks
    TaskServiceImpl taskService;

    @Test
    void delete_nonExisting_throws() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(id));
    }

    @Test
    void createTask_success_callsSaveAndReturns() {
        Task req = new Task();
        req.setTitle("T1");
        req.setProjectId(UUID.randomUUID());
        req.setCreatedBy(UUID.randomUUID());

        when(projectRepository.existsById(req.getProjectId())).thenReturn(true);

        var entity = new com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity();
        var saved = new com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity();
        saved.setId(UUID.randomUUID());

        when(taskMapper.toEntity(any())).thenReturn(entity);
        when(taskRepository.save(entity)).thenReturn(saved);
        when(taskMapper.toDomain(saved)).thenReturn(req);

        var res = taskService.createTask(req);
        assertNotNull(res);
        verify(taskRepository).save(entity);
    }

    @Test
    void getTasks_mapsAll() {
        var e1 = new com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity();
        var e2 = new com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity();
        when(taskRepository.findAll()).thenReturn(java.util.List.of(e1, e2));
        when(taskMapper.toDomain(e1)).thenReturn(new Task());
        when(taskMapper.toDomain(e2)).thenReturn(new Task());

        var list = taskService.getTasks(null, null, null, null, null);
        assertEquals(2, list.size());
    }

    @Test
    void updateTask_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException.class,
                () -> taskService.updateTask(id, new Task()));
    }

    @Test
    void deleteTask_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(false);
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException.class,
                () -> taskService.deleteTask(id));
    }
}
