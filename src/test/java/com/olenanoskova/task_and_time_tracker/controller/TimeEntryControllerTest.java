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

    @Test
    void getTimeEntry_returns4xx_whenServiceThrows() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(service.getTimeEntries(taskId)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException(taskId));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/tasks/{id}/time-entries", taskId.toString())).andExpect(status().is4xxClientError());
    }
}
