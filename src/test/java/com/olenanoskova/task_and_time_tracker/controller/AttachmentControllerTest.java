package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.service.AttachmentService;
import com.olenanoskova.task_and_time_tracker.mapper.AttachmentMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler;
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
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

    MockMvc mvc;

    @Mock
    AttachmentService service;

    @Mock
    AttachmentMapper mapper;

    @Mock
    SecurityService securityService;

    @InjectMocks
    AttachmentController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAttachment_returns404_whenNotFound() throws Exception{
        UUID taskId = UUID.randomUUID();
        lenient().when(service.getAttachmentsForTask(taskId)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException(taskId));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/tasks/{id}/attachments", taskId.toString())).andExpect(status().isNotFound());
    }
}
