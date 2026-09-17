package com.olenanoskova.task_and_time_tracker.controller;



import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectDeadlineMapper;
import com.olenanoskova.task_and_time_tracker.service.ProjectDeadlineService;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/deadlines")
@RequiredArgsConstructor
public class ProjectDeadlineController {

    private final ProjectDeadlineService projectDeadlineService;
    private final ProjectDeadlineMapper projectDeadlineMapper;

    @GetMapping
    public ResponseEntity<List<ProjectDeadlineResponseDto>> getAllDeadlines(@PathVariable UUID projectId) {
        List<ProjectDeadline> deadlines = projectDeadlineService.getAllDeadlines(projectId);
        List<ProjectDeadlineResponseDto> responseList = deadlines.stream()
                .map(projectDeadlineMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<ProjectDeadlineResponseDto> createDeadline(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectDeadlineCreateRequestDto request) {
        ProjectDeadline deadline = projectDeadlineMapper.toDomain(request, projectId);
        ProjectDeadline createdDeadline = projectDeadlineService.createDeadline(projectId, deadline);
        ProjectDeadlineResponseDto response = projectDeadlineMapper.toDto(createdDeadline);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
