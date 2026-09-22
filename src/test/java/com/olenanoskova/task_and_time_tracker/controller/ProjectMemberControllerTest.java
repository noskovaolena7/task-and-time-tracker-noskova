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
class ProjectMemberControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.ProjectMemberService service;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.ProjectMemberMapper mapper;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.ProjectMemberController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getMembers_returns4xx_whenServiceThrows() throws Exception {
        UUID projectId = UUID.randomUUID();
        when(service.getMembers(projectId)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException(projectId));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/projects/{id}/members", projectId.toString())).andExpect(status().is4xxClientError());
    }

    @Test
    void addMember_returns201_andJson() throws Exception {
        UUID projectId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberCreateRequestDto();
        req.setUserId(UUID.randomUUID());
        req.setMemberRoleDto(com.olenanoskova.task_and_time_tracker.controller.dto.MemberRoleDto.MANAGER);

        var domain = new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember();
        domain.setId(UUID.randomUUID());
        domain.setUserId(req.getUserId());
        domain.setMemberRole(com.olenanoskova.task_and_time_tracker.service.model.MemberRole.MANAGER);

        var resp = new com.olenanoskova.task_and_time_tracker.controller.dto.ProjectMemberResponseDto();
        resp.setId(domain.getId());
        resp.setUserId(domain.getUserId());

        when(mapper.toDomain(any(UUID.class), any())).thenReturn(domain);
        when(service.addMember(projectId, domain)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(resp);

        mvc.perform(post("/projects/{id}/members", projectId.toString())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(domain.getId().toString()))
                .andExpect(jsonPath("$.userId").value(domain.getUserId().toString()));
    }

    @Test
    void getMembers_returns200_emptyList() throws Exception {
        UUID projectId = UUID.randomUUID();
        when(service.getMembers(projectId)).thenReturn(java.util.List.of());

        mvc.perform(get("/projects/{id}/members", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deleteMember_returns204_onSuccess() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        // service.deleteMember does void; no exception -> success
        mvc.perform(delete("/projects/{id}/members/{userId}", projectId.toString(), userId.toString()))
                .andExpect(status().isNoContent());
    }
}
