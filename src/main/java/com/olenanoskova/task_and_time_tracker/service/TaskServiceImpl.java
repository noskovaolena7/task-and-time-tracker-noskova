package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.InvalidTaskStatusException;import com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.TaskMapper;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskStatusEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.TaskPriorityEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Task;
import com.olenanoskova.task_and_time_tracker.service.model.TaskStatus;
import com.olenanoskova.task_and_time_tracker.service.model.TaskPriority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public Task createTask(Task task) {

        log.info("Attempting to create task with title {}", task.getTitle());

        // Validate project
        if (!projectRepository.existsById(task.getProjectId())) {
            throw new ProjectNotFoundException(task.getProjectId());
        }

        // Validate assigned user
        if (task.getAssignedTo() != null && !userRepository.existsById(task.getAssignedTo())) {
            throw new UserNotFoundException(task.getAssignedTo());
        }

        // Default status
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.OPEN);
        }

        // Default priority
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }

        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        TaskEntity entity = taskMapper.toEntity(task);
        TaskEntity saved = taskRepository.save(entity);

        log.info("Successfully created task with title {}", task.getTitle());

        return taskMapper.toDomain(saved);
    }

    @Override
    public List<Task> getTasks(Integer page, Integer size, String status, UUID projectId, UUID assignedTo) {

        log.info("Fetching tasks page={}, size={}, status={}, projectId={}, assignedTo={}",
                page, size, status, projectId, assignedTo);

        List<TaskEntity> entities;

        if (status != null || projectId != null || assignedTo != null) {
            TaskStatusEntity statusEnum = null;
            if (status != null) {
                try {
                    statusEnum = TaskStatusEntity.valueOf(status);
                } catch (IllegalArgumentException e) {
                    throw new InvalidTaskStatusException(status);
                }
            }

            entities = taskRepository.findWithFilters(statusEnum, projectId, assignedTo);
        } else if (page != null && size != null) {
            entities = taskRepository.findAll(PageRequest.of(page, size)).getContent();
        } else {
            entities = taskRepository.findAll();
        }

        return entities.stream()
                .map(taskMapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> getTasksForUser(UUID userId, List<UUID> companyIds,
            Integer page, Integer size, String status, UUID projectId, UUID assignedTo) {

        log.info("Fetching visible tasks for user {}, projectId={}", userId, projectId);

        if (projectId != null) {
            return getTasks(page, size, status, projectId, assignedTo);
        }

        TaskStatusEntity statusEnum = null;
        if (status != null) {
            try {
                statusEnum = TaskStatusEntity.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new InvalidTaskStatusException(status);
            }
        }

        java.util.Set<UUID> visibleProjects = new java.util.HashSet<>();
        projectRepository.findByCompanyIdIsNullAndCreatedBy(userId)
                .forEach(p -> visibleProjects.add(p.getId()));
        if (companyIds != null) {
            for (UUID companyId : companyIds) {
                projectRepository.findByCompanyId(companyId)
                        .forEach(p -> visibleProjects.add(p.getId()));
            }
        }

        List<TaskEntity> entities = taskRepository.findWithFilters(statusEnum, null, assignedTo).stream()
                .filter(t -> visibleProjects.contains(t.getProjectId())
                        || userId.equals(t.getAssignedTo())
                        || userId.equals(t.getCreatedBy()))
                .toList();

        if (page != null && size != null) {
            int from = Math.min(page * size, entities.size());
            int to = Math.min(from + size, entities.size());
            entities = entities.subList(from, to);
        }

        return entities.stream()
                .map(taskMapper::toDomain)
                .toList();
    }

    @Override
    public Task getTaskById(UUID id) {

        log.info("Fetching task with id {}", id);

        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toDomain(entity);
    }

    @Override
    public Task updateTask(UUID id, Task task) {

        log.info("Attempting to update task with id {}", id);

        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        // Validate assigned user
        if (task.getAssignedTo() != null && !userRepository.existsById(task.getAssignedTo())) {
            throw new UserNotFoundException(task.getAssignedTo());
        }

        // Validate status
        if (task.getStatus() != null) {
            try {
                TaskStatus.valueOf(task.getStatus().name());
            } catch (IllegalArgumentException e) {
                throw new InvalidTaskStatusException(task.getStatus().name());
            }
        }

        // Update fields
        if (task.getTitle() != null) {
            entity.setTitle(task.getTitle());
        }

        if (task.getDescription() != null) {
            entity.setDescription(task.getDescription());
        }

        if (task.getProjectId() != null) {
            entity.setProjectId(task.getProjectId());
        }

        if (task.getStatus() != null) {
            entity.setStatus(TaskStatusEntity.valueOf(task.getStatus().name()));
        }

        if (task.getPriority() != null) {
            entity.setPriority(TaskPriorityEntity.valueOf(task.getPriority().name()));
        }

        if (task.getCreatedBy() != null) {
            entity.setCreatedBy(task.getCreatedBy());
        }

        if (task.getAssignedTo() != null) {
            entity.setAssignedTo(task.getAssignedTo());
        }

        if (task.getDueDate() != null) {
            entity.setDueDate(task.getDueDate());
        }

        if (task.getCompletedAt() != null) {
            entity.setCompletedAt(task.getCompletedAt());
        }

        entity.setUpdatedAt(Instant.now());

        TaskEntity saved = taskRepository.save(entity);

        log.info("Successfully updated task with id {}", id);

        return taskMapper.toDomain(saved);
    }

    @Override
    public void deleteTask(UUID id) {

        log.info("Attempting to delete task with id {}", id);

        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }

        taskRepository.deleteById(id);

        log.info("Successfully deleted task with id {}", id);
    }

    @Override
    public UUID getProjectIdByTaskId(UUID taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return task.getProjectId();
    }
}
