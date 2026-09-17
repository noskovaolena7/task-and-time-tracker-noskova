package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.TaskHistoryNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TaskHistoryMapper;
import com.olenanoskova.task_and_time_tracker.repository.TaskHistoryRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskHistoryEntity;
import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskRepository taskRepository;
    private final TaskHistoryMapper taskHistoryMapper;

    @Override
    public List<TaskHistory> getTaskHistory(UUID taskId) {

        log.info("Fetching history for task {}", taskId);

        // Check task exists
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        List<TaskHistoryEntity> entities = taskHistoryRepository.findByTaskId(taskId);

        return entities.stream()
                .map(taskHistoryMapper::toDomain)
                .toList();
    }

    @Override
    public TaskHistory getHistoryRecordById(UUID id) {

        log.info("Fetching task history record with id {}", id);

        TaskHistoryEntity entity = taskHistoryRepository.findById(id)
                .orElseThrow(() -> new TaskHistoryNotFoundException(id));

        return taskHistoryMapper.toDomain(entity);
    }
}
