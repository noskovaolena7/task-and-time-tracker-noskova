package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskReminderCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TaskReminderResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.TaskReminderMapper;
import com.olenanoskova.task_and_time_tracker.service.TaskReminderService;
import com.olenanoskova.task_and_time_tracker.service.model.TaskReminder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/reminders")
@RequiredArgsConstructor
public class TaskReminderController {

    private final TaskReminderService taskReminderService;
    private final TaskReminderMapper taskReminderMapper;

    @GetMapping
    @PreAuthorize("@securityService.canAccessTask(#taskId)")
    public ResponseEntity<List<TaskReminderResponseDto>> getReminders(@PathVariable UUID taskId) {

        List<TaskReminder> reminders = taskReminderService.getReminders(taskId);
        List<TaskReminderResponseDto> responseList = reminders.stream()
                .map(taskReminderMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("@securityService.canAccessTask(#taskId)")
    public ResponseEntity<TaskReminderResponseDto> createReminder(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskReminderCreateRequestDto request) {

        TaskReminder reminder = taskReminderMapper.toDomain(request, taskId);
        TaskReminder created = taskReminderService.addReminder(taskId, reminder);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskReminderMapper.toDto(created));
    }
}
