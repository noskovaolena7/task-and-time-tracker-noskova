package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.MessageByEmailRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.NotificationCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.NotificationResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.NotificationMapper;
import com.olenanoskova.task_and_time_tracker.service.NotificationService;
import com.olenanoskova.task_and_time_tracker.service.UserService;
import com.olenanoskova.task_and_time_tracker.service.model.Notification;
import com.olenanoskova.task_and_time_tracker.service.model.NotificationStatus;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final SecurityService securityService;
    private final UserService userService;

    @PostMapping("/users/{userId}/notifications")
    @PreAuthorize("@securityService.canMessageUser(#userId)")
    public ResponseEntity<NotificationResponseDto> sendMessage(
            @PathVariable UUID userId,
            @Valid @RequestBody NotificationCreateRequestDto request) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(securityService.getCurrentUserId());
        notification.setType("MESSAGE");
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.SENT);
        notification.setRead(false);

        Notification created = notificationService.createNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificationMapper.toDto(created));
    }

    @PostMapping("/users/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponseDto> sendMessageByEmail(
            @Valid @RequestBody MessageByEmailRequestDto request) {

        UUID recipientId = userService.getUserIdByEmail(request.getEmail());
        if (!securityService.canMessageUser(recipientId)) {
            throw new AccessDeniedException("Cannot message this user");
        }

        Notification notification = new Notification();
        notification.setUserId(recipientId);
        notification.setSenderId(securityService.getCurrentUserId());
        notification.setType("MESSAGE");
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.SENT);
        notification.setRead(false);

        Notification created = notificationService.createNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificationMapper.toDto(created));
    }

    @GetMapping("/users/{userId}/notifications")
    @PreAuthorize("@securityService.canAccessUser(#userId)")
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(
            @PathVariable UUID userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId, page, size);
        List<NotificationResponseDto> responseList = notifications.stream()
               .map(notificationMapper::toDto)
               .toList();

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/users/{userId}/notifications/{notificationId}/read")
    public ResponseEntity<NotificationResponseDto> markNotificationAsRead(@PathVariable UUID userId,
           @PathVariable UUID notificationId) {
        Notification existing = notificationService.getNotificationById(notificationId);
        if (!userId.equals(existing.getUserId())) {
            throw new AccessDeniedException("Notification does not belong to user " + userId);
        }
        Notification updated = notificationService.markNotificationAsRead(notificationId);
        NotificationResponseDto response = notificationMapper.toDto(updated);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/messages/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponseDto>> getSentMessages(@PathVariable UUID userId) {
        requireSelf(userId);
        List<NotificationResponseDto> responseList = notificationService.getSentMessages(userId).stream()
                .map(notificationMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/users/{userId}/notifications/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID userId,
            @PathVariable UUID notificationId) {
        requireSelf(userId);
        notificationService.deleteUserNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/conversations/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID userId,
            @PathVariable UUID otherUserId) {
        requireSelf(userId);
        notificationService.deleteConversation(userId, otherUserId);
        return ResponseEntity.noContent().build();
    }

    private void requireSelf(UUID userId) {
        if (!userId.equals(securityService.getCurrentUserId())) {
            throw new AccessDeniedException("Can only manage own messages");
        }
    }
}
