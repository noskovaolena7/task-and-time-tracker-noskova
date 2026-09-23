package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.InvalidReminderException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskReminderNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TaskReminderMapper;
import com.olenanoskova.task_and_time_tracker.repository.TaskReminderRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskReminderEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskReminderServiceTest {

    @Mock
    private TaskReminderRepository taskReminderRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskReminderMapper taskReminderMapper;

    @InjectMocks
    private TaskReminderServiceImpl taskReminderService;

    @Test
    void addReminder_taskNotFound_throws() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class,
                () -> taskReminderService.addReminder(taskId, new TaskReminder()));
    }

    @Test
    void addReminder_nullRemindAt_throws() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);

        TaskReminder reminder = new TaskReminder();
        reminder.setRemindAt(null);

        assertThrows(InvalidReminderException.class,
                () -> taskReminderService.addReminder(taskId, reminder));
    }

    @Test
    void addReminder_success() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.existsById(taskId)).thenReturn(true);

        TaskReminder reminder = new TaskReminder();
        reminder.setRemindAt(Instant.now());
        reminder.setMessage("Don't forget");

        TaskReminderEntity entity = new TaskReminderEntity();
        TaskReminderEntity saved = new TaskReminderEntity();
        TaskReminder domain = new TaskReminder();
        domain.setId(UUID.randomUUID());

        when(taskReminderMapper.toEntity(reminder)).thenReturn(entity);
        when(taskReminderRepository.save(entity)).thenReturn(saved);
        when(taskReminderMapper.toDomain(saved)).thenReturn(domain);

        TaskReminder res = taskReminderService.addReminder(taskId, reminder);
        assertNotNull(res.getId());
    }

    @Test
    void getReminders_returnsList() {
        UUID taskId = UUID.randomUUID();
        TaskReminderEntity entity = new TaskReminderEntity();
        TaskReminder domain = new TaskReminder();

        when(taskReminderRepository.findByTaskId(taskId)).thenReturn(List.of(entity));
        when(taskReminderMapper.toDomain(entity)).thenReturn(domain);

        List<TaskReminder> res = taskReminderService.getReminders(taskId);
        assertEquals(1, res.size());
    }

    @Test
    void getReminderById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(taskReminderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskReminderNotFoundException.class, () -> taskReminderService.getReminderById(id));
    }

    @Test
    void deleteReminder_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(taskReminderRepository.existsById(id)).thenReturn(false);

        assertThrows(TaskReminderNotFoundException.class, () -> taskReminderService.deleteReminder(id));
    }

    @Test
    void deleteReminder_success() {
        UUID id = UUID.randomUUID();
        when(taskReminderRepository.existsById(id)).thenReturn(true);

        taskReminderService.deleteReminder(id);
        verify(taskReminderRepository).deleteById(id);
    }
}
