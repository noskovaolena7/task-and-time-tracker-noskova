package com.olenanoskova.task_and_time_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectDeadlineMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.ProjectDeadlineService;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectDeadlineControllerTest {

    private MockMvc mvc;

    @Mock
    private ProjectDeadlineService projectDeadlineService;

    @Mock
    private ProjectDeadlineMapper projectDeadlineMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ProjectDeadlineController controller;

    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllDeadlines_returns200() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectDeadline pd = new ProjectDeadline();
        pd.setId(UUID.randomUUID());
        pd.setProjectId(projectId);
        pd.setDeadline(Instant.now().plusSeconds(3600));

        ProjectDeadlineResponseDto dto = new ProjectDeadlineResponseDto();
        dto.setId(pd.getId());
        dto.setProjectId(projectId);

        when(projectDeadlineService.getAllDeadlines(projectId)).thenReturn(List.of(pd));
        when(projectDeadlineMapper.toDto(pd)).thenReturn(dto);

        mvc.perform(get("/projects/{projectId}/deadlines", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(pd.getId().toString()));
    }

    @Test
    void createDeadline_returns201() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectDeadlineCreateRequestDto req = new ProjectDeadlineCreateRequestDto();
        req.setCreatedBy(UUID.randomUUID());
        req.setDeadline(Instant.now().plusSeconds(7200));
        req.setReminderPeriods(List.of("1_DAY_BEFORE"));

        ProjectDeadline domain = new ProjectDeadline();
        domain.setId(UUID.randomUUID());
        domain.setProjectId(projectId);

        ProjectDeadlineResponseDto dto = new ProjectDeadlineResponseDto();
        dto.setId(domain.getId());
        dto.setProjectId(projectId);

        when(projectDeadlineMapper.toDomain(any(ProjectDeadlineCreateRequestDto.class), eq(projectId))).thenReturn(domain);
        when(projectDeadlineService.createDeadline(eq(projectId), any(ProjectDeadline.class))).thenReturn(domain);
        when(projectDeadlineMapper.toDto(domain)).thenReturn(dto);

        mvc.perform(post("/projects/{projectId}/deadlines", projectId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(domain.getId().toString()));
    }

    @Test
    void updateDeadline_returns200() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID deadlineId = UUID.randomUUID();
        ProjectDeadlineUpdateRequestDto req = new ProjectDeadlineUpdateRequestDto();
        req.setDeadline(Instant.now().plusSeconds(14400));
        req.setTitle("Release milestone");

        ProjectDeadline updated = new ProjectDeadline();
        updated.setId(deadlineId);
        updated.setProjectId(projectId);

        ProjectDeadlineResponseDto dto = new ProjectDeadlineResponseDto();
        dto.setId(deadlineId);
        dto.setProjectId(projectId);

        when(projectDeadlineService.updateDeadline(eq(projectId), eq(deadlineId), any(ProjectDeadlineUpdateRequestDto.class))).thenReturn(updated);
        when(projectDeadlineMapper.toDto(updated)).thenReturn(dto);

        mvc.perform(put("/projects/{projectId}/deadlines/{deadlineId}", projectId.toString(), deadlineId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deadlineId.toString()));
    }

    @Test
    void deleteDeadline_returns204() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID deadlineId = UUID.randomUUID();

        mvc.perform(delete("/projects/{projectId}/deadlines/{deadlineId}", projectId.toString(), deadlineId.toString()))
                .andExpect(status().isNoContent());

        verify(projectDeadlineService).deleteDeadline(projectId, deadlineId);
    }
}
