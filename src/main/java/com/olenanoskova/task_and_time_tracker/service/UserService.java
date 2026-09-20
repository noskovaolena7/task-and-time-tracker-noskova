package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Role;
import com.olenanoskova.task_and_time_tracker.service.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    User registerPersonalUser(User user);
    
    User registerCompanyOwner(User user, UUID companyId);

    List<User> getUsers();

    User getUserById(UUID id);

    User activateUser(UUID id);

    User blockUser(UUID id);

    User updateUser(UUID id, User user);

    User updateRole(UUID userId, Role newRole);
    ;

    void delete(UUID id);

}