package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.TimeEntryMapper;
import com.olenanoskova.task_and_time_tracker.service.TimeEntryService;
import com.olenanoskova.task_and_time_tracker.service.model.TimeEntry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
    private final TimeEntryMapper timeEntryMapper;

    @GetMapping
    @PreAuthorize("@securityService.canAccessTask(#taskId)")
    public ResponseEntity<List<TimeEntryResponseDto>> getAllTimeEntriesForTask(@PathVariable UUID taskId) {
        List<TimeEntry> timeEntries = timeEntryService.getTimeEntries(taskId);
        List<TimeEntryResponseDto> responseList = timeEntries.stream()
                .map(timeEntryMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("@securityService.canAccessTask(#taskId)")
    public ResponseEntity<TimeEntryResponseDto> createTimeEntryForTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TimeEntryCreateRequestDto request) {

        TimeEntry timeEntry = timeEntryMapper.toDomain(request);
        TimeEntry createdTimeEntry = timeEntryService.createTimeEntry(taskId, timeEntry);
        TimeEntryResponseDto response = timeEntryMapper.toDto(createdTimeEntry);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
