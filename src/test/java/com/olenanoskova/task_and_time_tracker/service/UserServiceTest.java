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
    void createUser_success_callsSaveAndReturns() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
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
    void getUserById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }
}
