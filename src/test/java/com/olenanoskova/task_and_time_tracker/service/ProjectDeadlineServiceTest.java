package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.ProjectDeadlineUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.exception.InvalidDeadlineException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectDeadlineNotFoundException;
import com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectDeadlineMapper;
import com.olenanoskova.task_and_time_tracker.repository.ProjectDeadlineRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.ProjectDeadlineEntity;
import com.olenanoskova.task_and_time_tracker.service.model.ProjectDeadline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectDeadlineServiceTest {

    @Mock
    private ProjectDeadlineRepository projectDeadlineRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectDeadlineMapper projectDeadlineMapper;

    @InjectMocks
    private ProjectDeadlineServiceImpl projectDeadlineService;

    @Test
    void getAllDeadlines_returnsList() {
        UUID projectId = UUID.randomUUID();
        ProjectDeadlineEntity entity = new ProjectDeadlineEntity();
        ProjectDeadline domain = new ProjectDeadline();

        when(projectDeadlineRepository.findByProjectId(projectId)).thenReturn(List.of(entity));
        when(projectDeadlineMapper.toDomain(entity)).thenReturn(domain);

        List<ProjectDeadline> res = projectDeadlineService.getAllDeadlines(projectId);
        assertEquals(1, res.size());
    }

    @Test
    void createDeadline_projectNotFound_throws() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class,
                () -> projectDeadlineService.createDeadline(projectId, new ProjectDeadline()));
    }

    @Test
    void createDeadline_nullDeadline_throws() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(true);

        ProjectDeadline deadline = new ProjectDeadline();
        deadline.setDeadline(null);

        assertThrows(InvalidDeadlineException.class,
                () -> projectDeadlineService.createDeadline(projectId, deadline));
    }

    @Test
    void createDeadline_success() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(true);

        ProjectDeadline deadline = new ProjectDeadline();
        deadline.setDeadline(Instant.now());

        ProjectDeadlineEntity entity = new ProjectDeadlineEntity();
        ProjectDeadlineEntity saved = new ProjectDeadlineEntity();
        ProjectDeadline domain = new ProjectDeadline();
        domain.setId(UUID.randomUUID());

        when(projectDeadlineMapper.toEntity(deadline)).thenReturn(entity);
        when(projectDeadlineRepository.save(entity)).thenReturn(saved);
        when(projectDeadlineMapper.toDomain(saved)).thenReturn(domain);

        ProjectDeadline result = projectDeadlineService.createDeadline(projectId, deadline);
        assertNotNull(result.getId());
    }

    @Test
    void getDeadlineById_found() {
        UUID id = UUID.randomUUID();
        ProjectDeadlineEntity entity = new ProjectDeadlineEntity();
        ProjectDeadline domain = new ProjectDeadline();

        when(projectDeadlineRepository.findById(id)).thenReturn(Optional.of(entity));
        when(projectDeadlineMapper.toDomain(entity)).thenReturn(domain);

        ProjectDeadline res = projectDeadlineService.getDeadlineById(id);
        assertNotNull(res);
    }

    @Test
    void getDeadlineById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(projectDeadlineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProjectDeadlineNotFoundException.class,
                () -> projectDeadlineService.getDeadlineById(id));
    }

    @Test
    void updateDeadline_success() {
        UUID projectId = UUID.randomUUID();
        UUID deadlineId = UUID.randomUUID();
        ProjectDeadlineUpdateRequestDto req = new ProjectDeadlineUpdateRequestDto();
        req.setTitle("New Milestone");
        req.setDeadline(Instant.now());

        ProjectDeadlineEntity entity = new ProjectDeadlineEntity();
        entity.setId(deadlineId);
        entity.setProjectId(projectId);
        ProjectDeadline domain = new ProjectDeadline();
        domain.setId(deadlineId);

        when(projectDeadlineRepository.findById(deadlineId)).thenReturn(Optional.of(entity));
        when(projectDeadlineRepository.save(entity)).thenReturn(entity);
        when(projectDeadlineMapper.toDomain(entity)).thenReturn(domain);

        ProjectDeadline res = projectDeadlineService.updateDeadline(projectId, deadlineId, req);
        assertEquals(deadlineId, res.getId());
    }
}
