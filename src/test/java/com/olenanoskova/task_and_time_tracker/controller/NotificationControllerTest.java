package com.olenanoskova.task_and_time_tracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.NotificationService service;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.NotificationMapper mapper;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.NotificationController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllNotifications_returns4xx_whenServiceThrows() throws Exception {
        UUID userId = UUID.randomUUID();
        when(service.getNotificationsForUser(userId, null, null)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.NotificationNotFoundException(userId));

        mvc.perform(get("/users/{id}/notifications", userId.toString())).andExpect(status().is4xxClientError());
    }

    @Test
    void getAllNotifications_returns200_withList() throws Exception {
        UUID userId = UUID.randomUUID();
        var notif = new com.olenanoskova.task_and_time_tracker.service.model.Notification();
        notif.setId(UUID.randomUUID());
        notif.setMessage("You have a task assigned");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.NotificationResponseDto();
        dto.setId(notif.getId());
        dto.setMessage("You have a task assigned");

        when(service.getNotificationsForUser(userId, null, null)).thenReturn(java.util.List.of(notif));
        when(mapper.toDto(notif)).thenReturn(dto);

        mvc.perform(get("/users/{id}/notifications", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].message").value("You have a task assigned"));
    }

    @Test
    void markNotificationAsRead_returns200() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        var notif = new com.olenanoskova.task_and_time_tracker.service.model.Notification();
        notif.setId(notificationId);
        notif.setUserId(userId);
        notif.setRead(true);

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.NotificationResponseDto();
        dto.setId(notificationId);
        dto.setIsRead(true);

        when(service.getNotificationById(notificationId)).thenReturn(notif);
        when(service.markNotificationAsRead(notificationId)).thenReturn(notif);
        when(mapper.toDto(notif)).thenReturn(dto);

        mvc.perform(put("/users/{userId}/notifications/{notificationId}/read", userId.toString(), notificationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificationId.toString()))
                .andExpect(jsonPath("$.isRead").value(true));
    }
}
