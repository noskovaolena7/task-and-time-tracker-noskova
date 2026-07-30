package com.olenanoskova.task_and_time_tracker.controller;


import com.olenanoskova.task_and_time_tracker.dto.UserCreateRequest;
import com.olenanoskova.task_and_time_tracker.dto.UserResponse;
import com.olenanoskova.task_and_time_tracker.dto.UserUpdateRequest;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.model.Status;
import com.olenanoskova.task_and_time_tracker.model.User;
import com.olenanoskova.task_and_time_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {

        User user = userMapper.toDomain(request);
        User createdUser = userService.createUser(user);
        UserResponse response = userMapper.toDto(createdUser);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<User> users = userService.getUsers();
        List<UserResponse> responseList = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok().body(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {


        User user = userService.getUserById(UUID.fromString(id));
        UserResponse response = userMapper.toDto(user);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request
    ) {

        User updatedUser = userService.updateUser(UUID.fromString(id), request);
        UserResponse response = userMapper.toDto(updatedUser);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}