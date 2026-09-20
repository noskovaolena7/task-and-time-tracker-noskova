package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.TaskHistoryResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.TaskHistoryMapper;
import com.olenanoskova.task_and_time_tracker.service.TaskHistoryService;
import com.olenanoskova.task_and_time_tracker.service.model.TaskHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/history")
@RequiredArgsConstructor
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;
    private final TaskHistoryMapper taskHistoryMapper;

    @GetMapping
    @PreAuthorize("@securityService.canAccessTask(#taskId)")
    public ResponseEntity<List<TaskHistoryResponseDto>> getTaskHistory(@PathVariable UUID taskId) {
        List<TaskHistory> taskHistories = taskHistoryService.getTaskHistory(taskId);
        List<TaskHistoryResponseDto> taskHistoryDto = taskHistoryMapper.toDtoList(taskHistories);

        return ResponseEntity.ok(taskHistoryDto);
    }

}
