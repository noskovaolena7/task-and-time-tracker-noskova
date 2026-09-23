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
class TaskControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.TaskService service;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.TaskMapper mapper;

    @Mock
    com.olenanoskova.task_and_time_tracker.security.SecurityService securityService;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.TaskController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTask_returns4xx_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getTaskById(id)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException(id));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/tasks/{id}", id.toString())).andExpect(status().is4xxClientError());
    }

    @Test
    void createTask_returns201_andJson() throws Exception {
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.TaskCreateRequestDto();
        req.setTitle("Do it");
        req.setCreatedBy(UUID.randomUUID());
        req.setProjectId(UUID.randomUUID());
        req.setStatus("OPEN");
        req.setPriority("MEDIUM");

        var domain = new com.olenanoskova.task_and_time_tracker.service.model.Task();
        domain.setId(UUID.randomUUID());
        domain.setTitle("Do it");

        var resp = new com.olenanoskova.task_and_time_tracker.controller.dto.TaskResponseDto();
        resp.setId(domain.getId());
        resp.setTitle(domain.getTitle());

        when(mapper.toDomain(any(com.olenanoskova.task_and_time_tracker.controller.dto.TaskCreateRequestDto.class))).thenReturn(domain);
        when(service.createTask(domain)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(resp);

        mvc.perform(post("/tasks")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(domain.getId().toString()))
                .andExpect(jsonPath("$.title").value("Do it"));
    }

    @Test
    void getAllTasks_returns200_emptyList() throws Exception {
        when(service.getTasksForUser(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull()))
                .thenReturn(java.util.List.of());

        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
