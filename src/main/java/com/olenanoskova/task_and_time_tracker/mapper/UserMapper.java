package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.dto.UserCreateRequest;
import com.olenanoskova.task_and_time_tracker.dto.UserResponse;
import com.olenanoskova.task_and_time_tracker.dto.UserUpdateRequest;
import com.olenanoskova.task_and_time_tracker.model.Role;
import com.olenanoskova.task_and_time_tracker.model.User;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
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
    public User toDomain(UserUpdateRequest dto, User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(Role.valueOf(dto.getRole()));
        user.setUpdatedAt(Instant.now());

        return user;
    }

    public UserEntity toEntity(User user){

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setPhoneNumber(user.getPhoneNumber());
        entity.setRole(com.olenanoskova.task_and_time_tracker.repository.entity.Role.valueOf(user.getRole().name()));
        entity.setStatus(com.olenanoskova.task_and_time_tracker.repository.entity.Status.valueOf(
                user.getStatus().name()));
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        return entity;
    }

    public User toDomain(UserEntity entity){

        User user = new User();
        user.setId(entity.getId());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setPhoneNumber(entity.getPhoneNumber());
        user.setRole(com.olenanoskova.task_and_time_tracker.model.Role.valueOf(entity.getRole().name()));
        user.setStatus(com.olenanoskova.task_and_time_tracker.model.Status.valueOf(entity.getStatus().name()));
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());

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

}
