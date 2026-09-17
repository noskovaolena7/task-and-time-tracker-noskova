package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.NotificationResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.NotificationMapper;
import com.olenanoskova.task_and_time_tracker.service.NotificationService;
import com.olenanoskova.task_and_time_tracker.service.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(@PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getAllNotifications(userId);
        List<NotificationResponseDto> responseList = notifications.stream()
               .map(notificationMapper::toDto)
               .toList();

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/users/{userId}/notifications/{notificationId}/read")
    public ResponseEntity<NotificationResponseDto> markNotificationAsRead(@PathVariable UUID userId,
           @PathVariable UUID notificationId) {
        Notification updated = notificationService.markNotificationAsRead(notificationId);
        NotificationResponseDto response = notificationMapper.toDto(updated);

        return ResponseEntity.ok(response);
    }
}
