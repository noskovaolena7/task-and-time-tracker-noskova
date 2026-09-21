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
class UserControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.UserService userService;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.UserMapper userMapper;

    @Mock
    com.olenanoskova.task_and_time_tracker.security.SecurityService securityService;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.UserController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUserById_returns4xx_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(id)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException(id));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/users/{id}", id.toString())).andExpect(status().is4xxClientError());
    }


    @Test
    void getAllUsers_returns200_emptyList() throws Exception {
        UUID companyId = UUID.randomUUID();
        when(securityService.getCurrentUserId()).thenReturn(companyId);
        when(userService.getUsers(any(java.util.UUID.class))).thenReturn(java.util.List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
