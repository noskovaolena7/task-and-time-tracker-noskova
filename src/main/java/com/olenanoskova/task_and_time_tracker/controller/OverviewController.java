package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.UpcomingResponseDto;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.OverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/upcoming")
@RequiredArgsConstructor
public class OverviewController {

    private final OverviewService overviewService;
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UpcomingResponseDto> getUpcoming(
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "7") int days,
            @RequestParam(required = false, defaultValue = "20") int size) {

        if (!userId.equals(securityService.getCurrentUserId())) {
            throw new AccessDeniedException("Can only view own upcoming items");
        }

        return ResponseEntity.ok(overviewService.getUpcoming(userId, days, size));
    }
}
