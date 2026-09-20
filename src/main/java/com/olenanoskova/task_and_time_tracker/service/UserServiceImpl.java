package com.olenanoskova.task_and_time_tracker.service;


import com.olenanoskova.task_and_time_tracker.exception.UserAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.RoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.StatusEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import com.olenanoskova.task_and_time_tracker.service.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceService workspaceService;

    @Override
    public User createUser(User user) {

        log.info("Attempting to create a user with email {}", user.getEmail());

        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
    public User registerPersonalUser(User user) {

        log.info("Registering personal user with email {}", user.getEmail());

        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER); // 🔥 PERSONAL USER = USER
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        var workspace = workspaceService.createPersonalWorkspace(null);

        user.setWorkspaceId(workspace.getId());

        UserEntity saved = userRepository.save(userMapper.toEntity(user));

        log.info("Successfully registered personal user {}", user.getEmail());

        return userMapper.toDomain(saved);
    }

    @Override
    public User registerCompanyOwner(User user, UUID companyId) {

        log.info("Registering company owner with email {}", user.getEmail());

        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.OWNER); // 🔥 OWNER
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        var workspace = workspaceService.createCompanyWorkspace(null, companyId);

        user.setWorkspaceId(workspace.getId());

        UserEntity saved = userRepository.save(userMapper.toEntity(user));

        log.info("Successfully registered company owner {}", user.getEmail());

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

        entity.setStatus(StatusEntity.ACTIVE);
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public User blockUser(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        entity.setStatus(StatusEntity.BLOCKED);
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public User updateUser(UUID id, User user) {

        log.info("Attempting to update user with id {}", id);

        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        UserEntity userEntity = optionalUser.get();
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhoneNumber(user.getPhoneNumber());
        userEntity.setUpdatedAt(Instant.now());

        UserEntity savedUser = userRepository.save(userEntity);

        log.info("Successfully updated user with id {}", id);

        return userMapper.toDomain(savedUser);
    }

    @Override
    public void delete(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public User updateRole(UUID userId, Role newRole) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        entity.setRole(RoleEntity.valueOf(newRole.name()));

        UserEntity saved = userRepository.save(entity);

        return userMapper.toDomain(saved);
    }

}
