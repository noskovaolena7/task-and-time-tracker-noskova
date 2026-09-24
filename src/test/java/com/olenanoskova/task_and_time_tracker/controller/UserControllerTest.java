package com.olenanoskova.task_and_time_tracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    MockMvc mvc;

    @Mock
    com.olenanoskova.task_and_time_tracker.service.UserService userService;

    @Mock
    com.olenanoskova.task_and_time_tracker.mapper.UserMapper userMapper;

    @Mock
    com.olenanoskova.task_and_time_tracker.security.SecurityService securityService;

    @InjectMocks
    com.olenanoskova.task_and_time_tracker.controller.UserController controller;

    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.olenanoskova.task_and_time_tracker.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUserById_returns4xx_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(id)).thenThrow(new com.olenanoskova.task_and_time_tracker.exception.UserNotFoundException(id));

        mvc.perform(get("/users/{id}", id.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getUserById_returns200_whenFound() throws Exception {
        UUID id = UUID.randomUUID();
        var user = new com.olenanoskova.task_and_time_tracker.service.model.User();
        user.setId(id);
        user.setEmail("user@example.com");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.UserResponseDto();
        dto.setId(id);
        dto.setEmail("user@example.com");

        when(userService.getUserById(id)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        mvc.perform(get("/users/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void getUserByEmail_returns200_whenFound() throws Exception {
        UUID id = UUID.randomUUID();
        var user = new com.olenanoskova.task_and_time_tracker.service.model.User();
        user.setId(id);
        user.setEmail("friend@example.com");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.UserResponseDto();
        dto.setId(id);
        dto.setEmail("friend@example.com");

        when(userService.getUserIdByEmail("friend@example.com")).thenReturn(id);
        when(userService.getUserById(id)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        mvc.perform(get("/users/by-email").param("email", "friend@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("friend@example.com"));
    }

    @Test
    void getUserByEmail_returns404_whenUnknown() throws Exception {
        when(userService.getUserIdByEmail("ghost@example.com")).thenThrow(
                new com.olenanoskova.task_and_time_tracker.exception.ResourceNotFoundException("User with email ghost@example.com not found"));

        mvc.perform(get("/users/by-email").param("email", "ghost@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_returns200_emptyList() throws Exception {
        UUID companyId = UUID.randomUUID();
        when(securityService.getCurrentUserCompanyIds()).thenReturn(java.util.List.of(companyId));
        when(userService.getUsers(any(java.util.UUID.class))).thenReturn(java.util.List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void updateUser_returns200_whenValid() throws Exception {
        UUID id = UUID.randomUUID();
        var req = new com.olenanoskova.task_and_time_tracker.controller.dto.UserUpdateRequestDto();
        req.setFirstName("John");
        req.setLastName("Doe");

        var user = new com.olenanoskova.task_and_time_tracker.service.model.User();
        user.setId(id);
        var updated = new com.olenanoskova.task_and_time_tracker.service.model.User();
        updated.setId(id);
        updated.setFirstName("John");

        var dto = new com.olenanoskova.task_and_time_tracker.controller.dto.UserResponseDto();
        dto.setId(id);
        dto.setFirstName("John");

        when(userService.getUserById(id)).thenReturn(user);
        when(userService.updateUser(eq(id), any(com.olenanoskova.task_and_time_tracker.service.model.User.class))).thenReturn(updated);
        when(userMapper.toDto(updated)).thenReturn(dto);

        mvc.perform(put("/users/{id}", id.toString())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void deleteUser_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/users/{id}", id.toString()))
                .andExpect(status().isNoContent());
    }
}
