package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.NotificationNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.NotificationMapper;
import com.olenanoskova.task_and_time_tracker.repository.NotificationRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.NotificationEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.NotificationStatusEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Notification createNotification(Notification notification) {

        log.info("Attempting to create notification for user {}", notification.getUserId());

        if (!userRepository.existsById(notification.getUserId())) {
            throw new UserNotFoundException(notification.getUserId());
        }

        if (notification.getProjectId() != null &&
                !projectRepository.existsById(notification.getProjectId())) {
            throw new ProjectNotFoundException(notification.getProjectId());
        }

        if (notification.getTaskId() != null &&
                !taskRepository.existsById(notification.getTaskId())) {
            throw new TaskNotFoundException(notification.getTaskId());
        }

        notification.setCreatedAt(Instant.now());
        notification.setUpdatedAt(Instant.now());
        notification.setRead(false);

        NotificationEntity entity = notificationMapper.toEntity(notification);
        NotificationEntity saved = notificationRepository.save(entity);

        log.info("Successfully created notification for user {}", notification.getUserId());

        return notificationMapper.toDomain(saved);
    }

    @Override
    public List<Notification> getNotificationsForUser(UUID userId, Integer page, Integer size) {

        log.info("Fetching notifications for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<NotificationEntity> entities;

        if (page != null && size != null) {
            entities = notificationRepository.findByUserId(
                    userId,
                    PageRequest.of(page, size)
            );
        } else {
            entities = notificationRepository.findByUserId(userId);
        }

        return entities.stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    @Override
    public Notification getNotificationById(UUID id) {

        log.info("Fetching notification with id {}", id);

        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        return notificationMapper.toDomain(entity);
    }

    @Override
    public Notification markAsRead(UUID id) {

        log.info("Marking notification {} as read", id);

        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        entity.setRead(true);
        entity.setUpdatedAt(Instant.now());

        NotificationEntity saved = notificationRepository.save(entity);

        return notificationMapper.toDomain(saved);
    }

    @Override
    public Notification updateNotification(UUID id, Notification notification) {

        log.info("Attempting to update notification {}", id);

        Optional<NotificationEntity> optionalNotification = notificationRepository.findById(id);

        if (optionalNotification.isEmpty()) {
            throw new NotificationNotFoundException(id);
        }

        NotificationEntity entity = optionalNotification.get();

        if (notification.getMessage() != null) {
            entity.setMessage(notification.getMessage());
        }
        if (notification.getType() != null) {
            entity.setType(notification.getType());
        }
        if (notification.getScheduledAt() != null) {
            entity.setScheduledAt(notification.getScheduledAt());
        }
        if (notification.getSentAt() != null) {
            entity.setSentAt(notification.getSentAt());
        }
        if (notification.getStatus() != null) {
            entity.setStatus(NotificationStatusEntity.valueOf(notification.getStatus().name()));
        }
        entity.setUpdatedAt(Instant.now());

        NotificationEntity saved = notificationRepository.save(entity);

        log.info("Successfully updated notification {}", id);

        return notificationMapper.toDomain(saved);
    }

    @Override
    public void deleteNotification(UUID id) {

        log.info("Attempting to delete notification {}", id);

        if (!notificationRepository.existsById(id)) {
            throw new NotificationNotFoundException(id);
        }

        notificationRepository.deleteById(id);

        log.info("Successfully deleted notification {}", id);
    }
}
