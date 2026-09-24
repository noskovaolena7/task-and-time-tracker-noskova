package com.olenanoskova.task_and_time_tracker.service;


import com.olenanoskova.task_and_time_tracker.exception.BadRequestException;
import com.olenanoskova.task_and_time_tracker.exception.UserAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.repository.CompanyRepository;
import com.olenanoskova.task_and_time_tracker.repository.ProjectRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserCompanyRoleRepository;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.WorkspaceRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.RoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.StatusEntity;
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
    private final CompanyRepository companyRepository;
    private final UserCompanyRoleRepository userCompanyRoleRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;

    @Override
    public User createUser(User user) {

        user.setEmail(normalizeEmail(user.getEmail()));
        log.info("Attempting to create a user with email {}", user.getEmail());

        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException(user.getEmail());
        }

        if (user.getPhoneNumber() != null
                && userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new UserAlreadyExistException(user.getPhoneNumber());
        }

        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);

        log.info("Successfully created user with email {}", user.getEmail());

        return userMapper.toDomain(saved);
    }


    @Override
    public List<User> getUsers(UUID companyId) {
        List<UserEntity> entities = companyId == null
                ? userRepository.findAll()
                : userRepository.findAllByCompanyId(companyId);
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

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    @Override
    public UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .map(UserEntity::getId)
                .orElseThrow(() -> new com.olenanoskova.task_and_time_tracker.exception.ResourceNotFoundException(
                        "User with email " + email + " not found"));
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
    @org.springframework.transaction.annotation.Transactional
    public void delete(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        // Ownership must be transferred first: companies would be left
        // without an owner otherwise (their projects must remain).
        boolean ownsCompany = !companyRepository.findByOwnerId(id).isEmpty()
                || userCompanyRoleRepository.findCompanyIdsByUserId(id).stream()
                        .anyMatch(companyId -> userCompanyRoleRepository
                                .findByUserIdAndCompanyId(id, companyId)
                                .map(r -> "OWNER".equals(r.getRole().name()))
                                .orElse(false));
        if (ownsCompany) {
            throw new BadRequestException(
                    "Transfer company ownership to another owner before deleting your account");
        }

        // Personal data goes with the account: personal projects
        // (tasks, comments, deadlines, reminders cascade from them).
        projectRepository.findByCompanyIdIsNullAndCreatedBy(id)
                .forEach(p -> projectRepository.deleteById(p.getId()));

        // Company workspaces owned by the user pass to the company owner;
        // personal workspaces are deleted with the account.
        workspaceRepository.findByOwnerId(id).forEach(ws -> {
            if (ws.getCompanyId() != null) {
                companyRepository.findById(ws.getCompanyId()).ifPresentOrElse(
                        company -> {
                            ws.setOwnerId(company.getOwnerId());
                            ws.setUpdatedAt(Instant.now());
                            workspaceRepository.save(ws);
                        },
                        () -> workspaceRepository.deleteById(ws.getId()));
            } else {
                workspaceRepository.deleteById(ws.getId());
            }
        });

        // Company projects/tasks created by the user remain
        // (created_by -> NULL via ON DELETE SET NULL); role rows cascade.
        userRepository.deleteById(id);
    }

    @Override
    public User updateRole(UUID userId, Role newRole) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        entity.setRole(RoleEntity.valueOf(newRole.name()));

        UserEntity saved = userRepository.save(entity);

        return userMapper.toDomain(saved);
    }

    @Override
    public void updateUserWorkspace(UUID userId, UUID workspaceId) {

        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        entity.setWorkspaceId(workspaceId);
        entity.setUpdatedAt(Instant.now());

        userRepository.save(entity);
    }


}
