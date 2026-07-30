package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.dto.UserCreateRequest;
import com.olenanoskova.task_and_time_tracker.dto.UserResponse;
import com.olenanoskova.task_and_time_tracker.dto.UserUpdateRequest;
import com.olenanoskova.task_and_time_tracker.model.Role;
import com.olenanoskova.task_and_time_tracker.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
public class UserMapper {

    public User toDomain(UserCreateRequest dto){

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(Role.valueOf(dto.getRole()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return user;
    }

    public UserResponse toDto(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

    public User toDomain(UserUpdateRequest dto, User existingUser) {

        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setPhoneNumber(dto.getPhoneNumber());
        existingUser.setRole(Role.valueOf(dto.getRole()));
        existingUser.setUpdatedAt(Instant.now());

        return existingUser;
    }

}
