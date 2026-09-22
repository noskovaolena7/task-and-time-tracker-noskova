package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.UserResponseDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.UserUpdateRequestDto;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.security.SecurityService;
import com.olenanoskova.task_and_time_tracker.service.UserService;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityService securityService;


    @GetMapping
    @PreAuthorize("@securityService.canListUsers()")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {


        UUID companyId = securityService.getCurrentUserCompanyId();
        if (companyId == null) {
            throw new RuntimeException("Company not found");
        }
        List<User> users = userService.getUsers(companyId);
        List<UserResponseDto> responseList = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canAccessUser(#id)")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {

        log.info("Fetching user with id {}", id);

        User user = userService.getUserById(id);
        UserResponseDto response = userMapper.toDto(user);

        log.info("Successfully fetched user {}", id);

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

        log.info("Updating user {}", id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canDeleteUser(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {

        userService.delete(id);

        log.info("Deleting user {}", id);

        return ResponseEntity.noContent().build();
    }
}