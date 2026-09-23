package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.NotificationNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.NotificationMapper;
import com.olenanoskova.task_and_time_tracker.repository.NotificationRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.TaskRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.NotificationEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repo;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> service.getNotificationById(id));
    }

    @Test
    void getById_found() {
        UUID id = UUID.randomUUID();
        NotificationEntity entity = new NotificationEntity();
        Notification domain = new Notification();
        domain.setId(id);

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Notification result = service.getNotificationById(id);
        assertEquals(id, result.getId());
    }

    @Test
    void createNotification_userNotFound_throws() {
        UUID userId = UUID.randomUUID();
        Notification notif = new Notification();
        notif.setUserId(userId);

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.createNotification(notif));
    }

    @Test
    void createNotification_success() {
        UUID userId = UUID.randomUUID();
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setMessage("Test message");

        NotificationEntity entity = new NotificationEntity();
        NotificationEntity saved = new NotificationEntity();
        Notification domain = new Notification();
        domain.setId(UUID.randomUUID());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(mapper.toEntity(notif)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(domain);

        Notification created = service.createNotification(notif);
        assertNotNull(created.getId());
    }

    @Test
    void getAllNotifications_returnsList() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        NotificationEntity entity = new NotificationEntity();
        Notification domain = new Notification();

        when(repo.findByUserId(userId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Notification> list = service.getAllNotifications(userId);
        assertEquals(1, list.size());
    }

    @Test
    void markAsRead_success() {
        UUID id = UUID.randomUUID();
        NotificationEntity entity = new NotificationEntity();
        Notification domain = new Notification();
        domain.setId(id);
        domain.setRead(true);

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Notification res = service.markAsRead(id);
        assertTrue(res.isRead());
    }

    @Test
    void deleteNotification_success() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);

        service.deleteNotification(id);
        verify(repo).deleteById(id);
    }
}
