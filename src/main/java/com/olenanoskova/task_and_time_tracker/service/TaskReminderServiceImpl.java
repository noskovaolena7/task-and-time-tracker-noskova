package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.InvalidReminderException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskReminderNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TaskReminderMapper;
import com.olenanoskova.task_and_time_tracker.repository.TaskReminderRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskReminderEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskReminderServiceImpl implements TaskReminderService {

    private final TaskReminderRepository taskReminderRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskReminderMapper taskReminderMapper;

    @Override
    public TaskReminder addReminder(UUID taskId, TaskReminder reminder) {

        log.info("Attempting to add reminder for task {}", taskId);

        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        if (reminder.getCreatedBy() != null &&
                !userRepository.existsById(reminder.getCreatedBy())) {
            throw new UserNotFoundException(reminder.getCreatedBy());
        }

        if (reminder.getRemindAt() == null) {
            throw new InvalidReminderException("Reminder time cannot be null");
        }

        reminder.setTaskId(taskId);
        reminder.setCreatedAt(Instant.now());
        reminder.setUpdatedAt(Instant.now());

        TaskReminderEntity entity = taskReminderMapper.toEntity(reminder);
        TaskReminderEntity saved = taskReminderRepository.save(entity);

        log.info("Successfully added reminder for task {}", taskId);

        return taskReminderMapper.toDomain(saved);
    }

    @Override
    public List<TaskReminder> getReminders(UUID taskId) {

        log.info("Fetching reminders for task {}", taskId);

        List<TaskReminderEntity> entities = taskReminderRepository.findByTaskId(taskId);

        return entities.stream()
                .map(taskReminderMapper::toDomain)
                .toList();
    }

    @Override
    public TaskReminder getReminderById(UUID id) {

        log.info("Fetching reminder with id {}", id);

        TaskReminderEntity entity = taskReminderRepository.findById(id)
                .orElseThrow(() -> new TaskReminderNotFoundException(id));

        return taskReminderMapper.toDomain(entity);
    }

    @Override
    public TaskReminder updateReminder(UUID id, TaskReminder reminder) {

        log.info("Attempting to update reminder {}", id);

        Optional<TaskReminderEntity> optionalReminder = taskReminderRepository.findById(id);

        if (optionalReminder.isEmpty()) {
            throw new TaskReminderNotFoundException(id);
        }

        TaskReminderEntity entity = optionalReminder.get();

        if (reminder.getCreatedBy() != null &&
                !userRepository.existsById(reminder.getCreatedBy())) {
            throw new UserNotFoundException(reminder.getCreatedBy());
        }

        if (reminder.getRemindAt() == null) {
            throw new InvalidReminderException("Reminder time cannot be null");
        }

        entity.setCreatedBy(reminder.getCreatedBy());
        entity.setRemindAt(reminder.getRemindAt());
        entity.setMessage(reminder.getMessage());
        entity.setUpdatedAt(Instant.now());

        TaskReminderEntity saved = taskReminderRepository.save(entity);

        log.info("Successfully updated reminder {}", id);

        return taskReminderMapper.toDomain(saved);
    }

    @Override
    public void deleteReminder(UUID id) {

        log.info("Attempting to delete reminder {}", id);

        if (!taskReminderRepository.existsById(id)) {
            throw new TaskReminderNotFoundException(id);
        }

        taskReminderRepository.deleteById(id);

        log.info("Successfully deleted reminder {}", id);
    }
}
