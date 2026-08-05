package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.UserUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.exception.UserAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import com.olenanoskova.task_and_time_tracker.service.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User createUser(User user) {

        log.info("Attempting to create a user with email {}", user.getEmail());

        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException(user.getEmail());
        }

        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);

        log.info("Successfully created user with email {}", user.getEmail());

        return userMapper.toDomain(saved);
    }

    @Override
    public List<User> getUsers() {
        List<UserEntity> entities = userRepository.findAll();
        return entities.stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public User getUserById(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDomain(entity);
    }

    @Override
    public User activateUser(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        entity.setStatus(com.olenanoskova.task_and_time_tracker.repository.entity.Status.ACTIVE);
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public User blockUser(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        entity.setStatus(com.olenanoskova.task_and_time_tracker.repository.entity.Status.BLOCKED);
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public User updateUser(UUID id, UserUpdateRequestDto request) {

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User domain = userMapper.toDomain(entity);
        domain = userMapper.toDomain(request, domain);
        domain.setUpdatedAt(Instant.now());

        UserEntity saved = userRepository.save(userMapper.toEntity(domain));
        return userMapper.toDomain(saved);
    }

    @Override
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);

        if (!userRepository.existsById(uuid)) {
            throw new UserNotFoundException(uuid);
        }

        userRepository.deleteById(uuid);
    }
}
