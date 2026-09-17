package com.olenanoskova.task_and_time_tracker.mapper;

import com.olenanoskova.task_and_time_tracker.controller.dto.*;
import com.olenanoskova.task_and_time_tracker.repository.entity.RoleEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.StatusEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import com.olenanoskova.task_and_time_tracker.service.model.Status;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserMapper {

    // DTO → Domain (Create)
    public User toDomain(@Valid SignUpRequestDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }

    public User toDomain(UserCreateRequestDto dto) {

            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setPhoneNumber(dto.getPhoneNumber());
            return user;
    }

    // DTO → Domain (Update)
    public void updateDomain(UserUpdateRequestDto dto, User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUpdatedAt(Instant.now());
    }

    // Domain → Entity
    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setPhoneNumber(user.getPhoneNumber());
        entity.setRole(RoleEntity.valueOf(user.getRole().name()));
        entity.setStatus(StatusEntity.valueOf(user.getStatus().name()));
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }

    // Entity → Domain  ← ЭТОГО У ТЕБЯ НЕ ХВАТАЛО
    public User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setPhoneNumber(entity.getPhoneNumber());
        user.setRole(Role.valueOf(entity.getRole().name()));
        user.setStatus(Status.valueOf(entity.getStatus().name()));
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }

    // Domain → Response DTO
    public UserResponseDto toDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setMemberRole(MemberRoleDto.valueOf(user.getRole().name()));
        return dto;
    }
}


