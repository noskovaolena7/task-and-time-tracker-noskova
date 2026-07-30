package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.dto.UserUpdateRequest;
import com.olenanoskova.task_and_time_tracker.exception.UserAlreadyExistException;
import com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException;
import com.olenanoskova.task_and_time_tracker.model.Role;
import com.olenanoskova.task_and_time_tracker.model.Status;
import com.olenanoskova.task_and_time_tracker.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final HashMap<UUID, User> userHashMap = new HashMap<>();

    @Override
    public User createUser(User user) {

        log.info("Attempting to create a user with email {}", user.getEmail());

        // Перевірка на існування email
        Optional<User> optionalUser = userHashMap.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst();

        if (optionalUser.isPresent()) {
            log.error("User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistException(user.getEmail());
        }

        // Створення нового юзера
        user.setId(UUID.randomUUID());
        user.setStatus(Status.ACTIVE);
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        userHashMap.put(user.getId(), user);

        log.info("Successfully created a user with email {}", user.getEmail());

        return user;
    }

    @Override
    public List<User> getUsers() {

        log.info("Fetching all users");

        return userHashMap.values().stream().toList();
    }

    @Override
    public User getUserById(UUID userId) {

        log.info("Fetching user with id {}", userId);

        User user = userHashMap.get(userId);

        if (user != null) {
            log.info("User with id {} found", userId);
            return user;
        } else {
            log.error("User with id {} was not found", userId);
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public User activateUser(UUID userId) {

        log.info("Activating user with id {}", userId);

        User user = userHashMap.get(userId);

        if (user == null) {
            log.error("Cannot activate user. User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        user.setStatus(Status.ACTIVE);
        user.setUpdatedAt(Instant.now());
        userHashMap.put(userId, user);

        log.info("User with id {} successfully activated", userId);

        return user;
    }

    @Override
    public User blockUser(UUID userId) {

        log.info("Blocking user with id {}", userId);

        User user = userHashMap.get(userId);

        if (user == null) {
            log.error("Cannot block user. User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        user.setStatus(Status.BLOCKED);
        user.setUpdatedAt(Instant.now());
        userHashMap.put(userId, user);

        log.info("User with id {} successfully blocked", userId);

        return user;
    }

    @Override
    public User updateUser(UUID userId, @Valid UserUpdateRequest request) {

        log.info("Updating user with id {}", userId);

        User user = userHashMap.get(userId);

        if (user == null) {
            log.error("Cannot update user. User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        user.setUpdatedAt(Instant.now());
        userHashMap.put(userId, user);

        log.info("User with id {} successfully updated", userId);

        return user;
    }

    @Override
    public void delete(String id) {
        UUID userId = UUID.fromString(id);

        log.info("Deleting user with id {}", userId);

        User removedUser = userHashMap.remove(userId);

        if (removedUser == null) {
            log.error("Cannot delete user. User with id {} not found", userId);
            throw new UserNotFoundException(userId);
        }

        log.info("User with id {} successfully deleted", userId);
    }

}
