package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification createNotification(Notification notification);

    List<Notification> getNotificationsForUser(UUID userId, Integer page, Integer size);

    default List<Notification> getAllNotifications(UUID userId) {
        return getNotificationsForUser(userId, null, null);
    }

    Notification getNotificationById(UUID id);

    Notification markAsRead(UUID id);

    default Notification markNotificationAsRead(UUID id) {
        return markAsRead(id);
    }

    Notification updateNotification(UUID id, Notification notification);

    void deleteNotification(UUID id);
}
