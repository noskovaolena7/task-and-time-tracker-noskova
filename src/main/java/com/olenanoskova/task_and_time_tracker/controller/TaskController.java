package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.*;
import com.olenanoskova.task_and_time_tracker.mapper.TaskMapper;
import com.olenanoskova.task_and_time_tracker.service.TaskService;
import com.olenanoskova.task_and_time_tracker.service.model.Task;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;


    @PostMapping
    @PreAuthorize("@securityService.canCreateTask(#request.projectId)")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateRequestDto request) {
        Task task = taskMapper.toDomain(request);
        Task createdTask = taskService.createTask(task);
        TaskResponseDto response = taskMapper.toDto(createdTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("@securityService.canAccessTask(#projectId)")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID assignedTo) {

        List<Task> tasks = taskService.getTasks(page, size, status, projectId, assignedTo);
        List<TaskResponseDto> responseList = tasks.stream()
                .map(taskMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessTask(#id)")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable UUID id) {
        Task task = taskService.getTaskById(id);
        TaskResponseDto response = taskMapper.toDto(task);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canAccessTask(#id)")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskUpdateRequestDto request) {
        Task task = taskService.getTaskById(id);
        taskMapper.updateDomain(request, task);
        Task updatedTask = taskService.updateTask(id, task);
        TaskResponseDto response = taskMapper.toDto(updatedTask);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canDeleteTask(#id)")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}


