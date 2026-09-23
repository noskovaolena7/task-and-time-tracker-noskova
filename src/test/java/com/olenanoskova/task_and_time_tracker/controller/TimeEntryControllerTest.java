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
class TimeEntryControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.TimeEntryService service;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.TimeEntryMapper mapper;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.TimeEntryController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    private final com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    void getTimeEntry_returns4xx_whenServiceThrows() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(service.getTimeEntries(taskId)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException(taskId));

        mvc.perform(get("/tasks/{id}/time-entries", taskId.toString())).andExpect(status().is4xxClientError());
    }

    @Test
    void getAllTimeEntriesForTask_returns200() throws Exception {
        UUID taskId = UUID.randomUUID();
        var entry = new com.olenanoskova.task_and_time_tracker.service.model.TimeEntry();
        entry.setId(UUID.randomUUID());
        entry.setTaskId(taskId);

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryResponseDto();
        dto.setId(entry.getId());
        dto.setTaskId(taskId);

        when(service.getTimeEntries(taskId)).thenReturn(java.util.List.of(entry));
        when(mapper.toDto(entry)).thenReturn(dto);

        mvc.perform(get("/tasks/{id}/time-entries", taskId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(entry.getId().toString()));
    }

    @Test
    void createTimeEntryForTask_returns201() throws Exception {
        UUID taskId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryCreateRequestDto();
        req.setTaskId(taskId);
        req.setUserId(UUID.randomUUID());
        req.setStartTime(java.time.Instant.now().minusSeconds(3600));
        req.setEndTime(java.time.Instant.now());

        var entry = new com.olenanoskova.task_and_time_tracker.service.model.TimeEntry();
        entry.setId(UUID.randomUUID());
        entry.setTaskId(taskId);

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryResponseDto();
        dto.setId(entry.getId());
        dto.setTaskId(taskId);

        when(mapper.toDomain(any(com.olenanoskova.task_and_time_tracker.controller.dto.TimeEntryCreateRequestDto.class))).thenReturn(entry);
        when(service.createTimeEntry(org.mockito.ArgumentMatchers.eq(taskId), any(com.olenanoskova.task_and_time_tracker.service.model.TimeEntry.class))).thenReturn(entry);
        when(mapper.toDto(entry)).thenReturn(dto);

        mvc.perform(post("/tasks/{id}/time-entries", taskId.toString())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(entry.getId().toString()));
    }
}
