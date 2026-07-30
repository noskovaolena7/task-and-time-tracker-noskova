package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.dto.UserUpdateRequest;
import com.olenanoskova.task_and_time_tracker.model.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    List<User> getUsers();

    User getUserById(UUID id);

    User activateUser(UUID id);

    User blockUser(UUID id);

    User updateUser(UUID uuid, @Valid UserUpdateRequest request);

    void delete(String id);

}