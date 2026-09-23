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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.CommentService service;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.CommentMapper mapper;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.CommentController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    private final com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @Test
    void getAll_returns4xx_whenServiceThrows() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(service.getComments(taskId)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.TaskNotFoundException(taskId));

        mvc.perform(get("/tasks/{id}/comments", taskId.toString())).andExpect(status().is4xxClientError());
    }

    @Test
    void getAll_returns200_withComments() throws Exception {
        UUID taskId = UUID.randomUUID();
        var comment = new com.olenanoskova.task_and_time_tracker.service.model.Comment();
        comment.setId(UUID.randomUUID());
        comment.setTaskId(taskId);
        comment.setText("Great progress");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText("Great progress");

        when(service.getComments(taskId)).thenReturn(java.util.List.of(comment));
        when(mapper.toDto(comment)).thenReturn(dto);

        mvc.perform(get("/tasks/{id}/comments", taskId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text").value("Great progress"));
    }

    @Test
    void createComment_returns201_onSuccess() throws Exception {
        UUID taskId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.CommentCreateRequestDto();
        req.setUserId(UUID.randomUUID());
        req.setText("New comment");

        var comment = new com.olenanoskova.task_and_time_tracker.service.model.Comment();
        comment.setId(UUID.randomUUID());
        comment.setText("New comment");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText("New comment");

        when(mapper.toDomain(any(com.olenanoskova.task_and_time_tracker.controller.dto.CommentCreateRequestDto.class))).thenReturn(comment);
        when(service.createComment(eq(taskId), any(com.olenanoskova.task_and_time_tracker.service.model.Comment.class))).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(dto);

        mvc.perform(post("/tasks/{id}/comments", taskId.toString())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(comment.getId().toString()))
                .andExpect(jsonPath("$.text").value("New comment"));
    }

    @Test
    void deleteComment_returns204_onSuccess() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        var comment = new com.olenanoskova.task_and_time_tracker.service.model.Comment();
        comment.setId(commentId);
        comment.setTaskId(taskId);

        when(service.getCommentById(commentId)).thenReturn(comment);

        mvc.perform(delete("/tasks/{taskId}/comments/{commentId}", taskId.toString(), commentId.toString()))
                .andExpect(status().isNoContent());
        org.mockito.Mockito.verify(service).deleteComment(commentId);
    }

    @Test
    void deleteComment_returns404_whenTaskMismatch() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        var comment = new com.olenanoskova.task_and_time_tracker.service.model.Comment();
        comment.setId(commentId);
        comment.setTaskId(UUID.randomUUID());

        when(service.getCommentById(commentId)).thenReturn(comment);

        mvc.perform(delete("/tasks/{taskId}/comments/{commentId}", taskId.toString(), commentId.toString()))
                .andExpect(status().is4xxClientError());
    }
}
