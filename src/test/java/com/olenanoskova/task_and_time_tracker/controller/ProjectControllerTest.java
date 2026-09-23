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
class ProjectControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.ProjectService projectService;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.ProjectMapper projectMapper;

    @Mock
    com.olenanoskova.task_and_time_tracker.security.SecurityService securityService;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.ProjectController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getProjectById_returns4xx_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(projectService.getProjectById(id)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException(id));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/projects/{id}", id.toString())).andExpect(status().is4xxClientError());
    }

    @Test
    void createProject_returns201_andJson() throws Exception {
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.ProjectCreateRequestDto();
        req.setCompanyId(UUID.randomUUID());
        req.setName("ProjX");
        req.setCreatedBy(UUID.randomUUID());

        var domain = new com.olenanoskova.task_and_time_tracker.service.model.Project();
        domain.setId(UUID.randomUUID());
        domain.setName("ProjX");

        var resp = new com.olenanoskova.task_and_time_tracker.controller.dto.ProjectResponseDto();
        resp.setId(domain.getId());
        resp.setName(domain.getName());
        resp.setCreatedAt(java.time.Instant.now());

        when(projectMapper.toDomain(any(com.olenanoskova.task_and_time_tracker.controller.dto.ProjectCreateRequestDto.class))).thenReturn(domain);
        when(projectService.createProject(domain)).thenReturn(domain);
        when(projectMapper.toDto(domain)).thenReturn(resp);

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/projects")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(domain.getId().toString()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("ProjX"));
    }

    @Test
    void getAllProjects_returns200_emptyList() throws Exception {
        // No company on the mocked security context -> personal listing branch.
        when(projectService.getPersonalProjects(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(0),
                org.mockito.ArgumentMatchers.eq(20)))
                .thenReturn(java.util.List.of());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/projects"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray());
    }
}
