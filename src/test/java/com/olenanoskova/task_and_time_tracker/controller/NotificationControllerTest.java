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
        when(service.getAllNotifications(userId)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.NotificationNotFoundException(userId));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/users/{id}/notifications", userId.toString())).andExpect(status().is4xxClientError());
    }
}
