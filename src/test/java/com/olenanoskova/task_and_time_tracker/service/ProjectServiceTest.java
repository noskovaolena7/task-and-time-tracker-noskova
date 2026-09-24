package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMapper;
import com.olenanoskova.task_and_time_tracker.service.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.CompanyRepository companyRepository;

    @Mock
    ProjectMapper projectMapper;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    void getProjectById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> projectService.getProjectById(id));
    }

    @Test
    void createProject_companyMissing_throws() {
        var p = new com.olenanoskova.task_and_time_tracker.service.model.Project();
        p.setCompanyId(UUID.randomUUID());

        when(companyRepository.existsById(p.getCompanyId())).thenReturn(false);
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.CompanyNotFoundException.class,
                () -> projectService.createProject(p));
    }

    @Test
    void createProject_duplicate_throws() {
        var p = new com.olenanoskova.task_and_time_tracker.service.model.Project();
        p.setCompanyId(UUID.randomUUID());
        p.setName("X");

        when(companyRepository.existsById(p.getCompanyId())).thenReturn(true);
        when(projectRepository.findByNameAndCompanyId("X", p.getCompanyId())).thenReturn(Optional.of(new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity()));

        assertThrows(com.olenanoskova.task_and_time_tracker.exception.ProjectAlreadyExistException.class,
                () -> projectService.createProject(p));
    }

    @Test
    void getProjects_mapsAll() {
        var e1 = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity();
        var e2 = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity();
        when(projectRepository.findAll()).thenReturn(java.util.List.of(e1, e2));
        when(projectMapper.toDomain(e1)).thenReturn(new com.olenanoskova.task_and_time_tracker.service.model.Project());
        when(projectMapper.toDomain(e2)).thenReturn(new com.olenanoskova.task_and_time_tracker.service.model.Project());

        var list = projectService.getProjects(null, null, null);
        assertEquals(2, list.size());
    }

    @Test
    void updateProject_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException.class,
                () -> projectService.updateProject(id, new com.olenanoskova.task_and_time_tracker.service.model.Project()));
    }

    @Test
    void deleteProject_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(false);
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException.class,
                () -> projectService.deleteProject(id));
    }

    @Test
    void getMemberProjects_returnsMemberProjects() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        var entity = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity();
        entity.setId(projectId);
        var domain = new com.olenanoskova.task_and_time_tracker.service.model.Project();
        domain.setId(projectId);

        when(projectMemberRepository.findProjectIdsByUserId(userId)).thenReturn(java.util.List.of(projectId));
        when(projectRepository.findAllById(java.util.List.of(projectId))).thenReturn(java.util.List.of(entity));
        when(projectMapper.toDomain(entity)).thenReturn(domain);

        var list = projectService.getMemberProjects(userId);
        assertEquals(1, list.size());
        assertEquals(projectId, list.get(0).getId());
    }

}
