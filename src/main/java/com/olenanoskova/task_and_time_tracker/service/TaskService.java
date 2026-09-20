package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.entity.TaskEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task createTask(Task task);

    List<Task> getTasks(Integer page, Integer size, String status, UUID projectId, UUID assignedTo);

    Task getTaskById(UUID id);

    Task updateTask(UUID id, Task task);

    void deleteTask(UUID id);

    UUID getProjectIdByTaskId(UUID taskId);


}
