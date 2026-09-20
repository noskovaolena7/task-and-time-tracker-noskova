package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.UserCreateRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.service.UserService;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @PreAuthorize("@securityService.canCreateUser(#request.companyId)")
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserCreateRequestDto request) {

        User user = userMapper.toDomain(request);
        User createdUser = userService.createUser(user);
        UserResponseDto response = userMapper.toDto(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("@securityService.canListUsers()")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        List<User> users = userService.getUsers();
        List<UserResponseDto> responseList = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessUser(#id)")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {

        User user = userService.getUserById(id);
        UserResponseDto response = userMapper.toDto(user);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityService.canUpdateUser(#id)")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDto request) {

        User user = userService.getUserById(id);
        userMapper.updateDomain(request, user);
        User updatedUser = userService.updateUser(id, user);
        UserResponseDto response = userMapper.toDto(updatedUser);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canDeleteUser(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}