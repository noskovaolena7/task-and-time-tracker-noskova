package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.ProjectMemberRepository;
import com.olenanoskova.task_and_time_tracker.mapper.ProjectMemberMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    ProjectMemberRepository repo;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.ProjectRepository projectRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.UserRepository userRepository;

    @Mock
    ProjectMemberMapper mapper;

    @InjectMocks
    ProjectMemberServiceImpl service;

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getMemberById(id));
    }

    @Test
    void addMember_projectMissing_throws() {
        UUID projectId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember();
        req.setUserId(UUID.randomUUID());

        when(projectRepository.existsById(projectId)).thenReturn(false);
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.ProjectNotFoundException.class,
                () -> service.addMember(projectId, req));
    }

    @Test
    void addMember_userMissing_throws() {
        UUID projectId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember();
        req.setUserId(UUID.randomUUID());

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(userRepository.existsById(req.getUserId())).thenReturn(false);

        assertThrows(com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException.class,
                () -> service.addMember(projectId, req));
    }

    @Test
    void addMember_duplicate_throws() {
        UUID projectId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember();
        req.setUserId(UUID.randomUUID());

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(userRepository.existsById(req.getUserId())).thenReturn(true);
        when(repo.findByProjectIdAndUserId(projectId, req.getUserId())).thenReturn(Optional.of(new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity()));

        assertThrows(com.olenanoskova.task_and_time_tracker.exception.ProjectMemberAlreadyExistsException.class,
                () -> service.addMember(projectId, req));
    }

    @Test
    void addMember_success_savesAndReturns() {
        UUID projectId = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember();
        req.setUserId(UUID.randomUUID());
        req.setMemberRole(com.olenanoskova.task_and_time_tracker.service.model.MemberRole.MANAGER);

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(userRepository.existsById(req.getUserId())).thenReturn(true);
        when(repo.findByProjectIdAndUserId(projectId, req.getUserId())).thenReturn(Optional.empty());

        var entity = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity();
        var saved = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity();
        saved.setId(UUID.randomUUID());

        when(mapper.toEntity(any())).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(req);

        var res = service.addMember(projectId, req);
        assertNotNull(res);
        verify(repo).save(entity);
    }

    @Test
    void deleteMember_notFound_throws() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(repo.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());
        assertThrows(com.olenanoskova.task_and_time_tracker.exception.ProjectMemberNotFoundException.class,
                () -> service.deleteMember(projectId, userId));
    }

    @Test
    void getMembers_mapsAll() {
        UUID projectId = UUID.randomUUID();
        var e1 = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity();
        var e2 = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectMemberEntity();
        when(repo.findByProjectId(projectId)).thenReturn(java.util.List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember());
        when(mapper.toDomain(e2)).thenReturn(new com.olenanoskova.task_and_time_tracker.service.model.ProjectMember());

        var list = service.getMembers(projectId);
        assertEquals(2, list.size());
    }

}
