package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.UserAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.CompanyRepository companyRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository userCompanyRoleRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.ProjectRepository projectRepository;

    @Mock
    com.olenanoskova.task_and_time_tracker.repository.WorkspaceRepository workspaceRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("a@b.com");
    }

    @Test
    void createUser_whenEmailExists_throws() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new UserEntity()));
        assertThrows(UserAlreadyExistException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void createUser_success_callsSaveAndReturns() {        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        UserEntity entity = new UserEntity();
        UserEntity saved = new UserEntity();
        saved.setId(UUID.randomUUID());
        when(userMapper.toEntity(any())).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        User savedDomain = new User();
        savedDomain.setId(saved.getId());
        when(userMapper.toDomain(saved)).thenReturn(savedDomain);

        User result = userService.createUser(user);
        assertNotNull(result.getId());
        verify(userRepository).save(entity);
    }

    @Test
    void createUser_normalizesEmail() {
        user.setEmail("  Mia@Example.COM ");
        when(userRepository.findByEmail("mia@example.com")).thenReturn(Optional.empty());
        UserEntity entity = new UserEntity();
        UserEntity saved = new UserEntity();
        saved.setId(UUID.randomUUID());
        when(userMapper.toEntity(any())).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDomain(saved)).thenReturn(new User());

        userService.createUser(user);

        assertEquals("mia@example.com", user.getEmail());
        verify(userRepository).findByEmail("mia@example.com");
    }

    @Test
    void getUserIdByEmail_normalizesEmail() {
        UUID id = UUID.randomUUID();
        UserEntity entity = new UserEntity();
        entity.setId(id);
        when(userRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(entity));

        assertEquals(id, userService.getUserIdByEmail(" Friend@Example.com "));
    }

    @Test
    void getUserById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void delete_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(id));
    }

    @Test
    void delete_ownerWithoutTransfer_throws() {
        UUID id = UUID.randomUUID();
        var company = new com.olenanoskova.task_and_time_tracker.repository.entity.CompanyEntity();
        company.setId(UUID.randomUUID());
        company.setOwnerId(id);
        when(userRepository.existsById(id)).thenReturn(true);
        when(companyRepository.findByOwnerId(id)).thenReturn(java.util.List.of(company));

        assertThrows(com.olenanoskova.task_and_time_tracker.exception.BadRequestException.class,
                () -> userService.delete(id));
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void delete_personalUser_purgesPersonalData() {
        UUID id = UUID.randomUUID();
        var project = new com.olenanoskova.task_and_time_tracker.repository.entity.ProjectEntity();
        project.setId(UUID.randomUUID());
        var ws = new com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity();
        ws.setId(UUID.randomUUID());
        ws.setOwnerId(id);
        ws.setCompanyId(null);
        when(userRepository.existsById(id)).thenReturn(true);
        when(companyRepository.findByOwnerId(id)).thenReturn(java.util.List.of());
        when(userCompanyRoleRepository.findCompanyIdsByUserId(id)).thenReturn(java.util.List.of());
        when(projectRepository.findByCompanyIdIsNullAndCreatedBy(id))
                .thenReturn(java.util.List.of(project));
        when(workspaceRepository.findByOwnerId(id)).thenReturn(java.util.List.of(ws));

        userService.delete(id);

        verify(projectRepository).deleteById(project.getId());
        verify(workspaceRepository).deleteById(ws.getId());
        verify(userRepository).deleteById(id);
    }
}
