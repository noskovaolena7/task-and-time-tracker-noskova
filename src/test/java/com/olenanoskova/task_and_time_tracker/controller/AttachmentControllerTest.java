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
        mvc.perform(get("/tasks/{id}/attachments", taskId.toString())).andExpect(status().isNotFound());
    }

    @Test
    void getAllAttachments_returns200() throws Exception {
        UUID taskId = UUID.randomUUID();
        var att = new com.olenanoskova.task_and_time_tracker.service.model.Attachment();
        att.setId(UUID.randomUUID());
        att.setTaskId(taskId);
        att.setFileName("doc.pdf");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.AttachmentResponseDto();
        dto.setId(att.getId());
        dto.setFileName("doc.pdf");

        when(service.getAttachmentsForTask(taskId)).thenReturn(java.util.List.of(att));
        when(mapper.toDto(att)).thenReturn(dto);

        mvc.perform(get("/tasks/{id}/attachments", taskId.toString()))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].fileName").value("doc.pdf"));
    }

    @Test
    void deleteAttachment_returns204() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID attId = UUID.randomUUID();

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/tasks/{taskId}/attachments/{attachmentId}", taskId.toString(), attId.toString()))
                .andExpect(status().isNoContent());
    }
}
