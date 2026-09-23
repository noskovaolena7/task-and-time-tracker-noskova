package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterCompanyRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterUserRequestDto;
import com.olenanoskova.task_and_time_tracker.exception.AccountIsBlockedException;
import com.olenanoskova.task_and_time_tracker.exception.InvalidCredentialsException;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;
import com.olenanoskova.task_and_time_tracker.service.model.Company;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import com.olenanoskova.task_and_time_tracker.service.model.Status;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private CompanyService companyService;

    @Mock
    private UserCompanyRoleService userCompanyRoleService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private WorkspaceService workspaceService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signUpPersonalUser_success() {
        var req = new RegisterUserRequestDto();
        req.setEmail("test@personal.com");
        req.setPassword("rawPassword");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@personal.com");
        user.setPassword("rawPassword");
        user.setRole(Role.PERSONAL_USER);

        when(userMapper.toDomain(req)).thenReturn(user);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userService.createUser(user)).thenReturn(user);

        WorkspaceEntity ws = new WorkspaceEntity();
        ws.setId(UUID.randomUUID());
        when(workspaceService.createPersonalWorkspace(user.getId())).thenReturn(ws);
        when(tokenService.createToken(user.getId().toString(), Role.PERSONAL_USER)).thenReturn("jwt-token-123");

        String token = authService.signUpPersonalUser(req);

        assertEquals("jwt-token-123", token);
        verify(userService).updateUserWorkspace(user.getId(), ws.getId());
    }

    @Test
    void signUpCompanyUser_success() {
        var req = new RegisterCompanyRequestDto();
        req.setEmail("owner@comp.com");
        req.setPassword("pass123");
        req.setCompanyName("Acme Corp");
        req.setCompanyDescription("Building stuff");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("owner@comp.com");
        user.setPassword("pass123");

        when(userMapper.toDomain(req)).thenReturn(user);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded123");
        when(userService.createUser(user)).thenReturn(user);

        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Acme Corp");
        when(companyService.createCompany(any(Company.class))).thenReturn(company);

        WorkspaceEntity ws = new WorkspaceEntity();
        ws.setId(UUID.randomUUID());
        when(workspaceService.createCompanyWorkspace(user.getId(), company.getId())).thenReturn(ws);
        when(tokenService.createToken(user.getId().toString(), Role.OWNER)).thenReturn("owner-jwt");

        String token = authService.signUpCompanyUser(req);

        assertEquals("owner-jwt", token);
        verify(userService).updateRole(user.getId(), Role.OWNER);
    }

    @Test
    void login_success() {
        String email = "login@example.com";
        String password = "mypassword";

        UserEntity entity = new UserEntity();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword("hashed");
        user.setRole(Role.PERSONAL_USER);
        user.setStatus(Status.ACTIVE);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(user);
        when(passwordEncoder.matches(password, "hashed")).thenReturn(true);
        when(tokenService.createToken(user.getId().toString(), Role.PERSONAL_USER)).thenReturn("logged-in-token");

        String token = authService.login(email, password);
        assertEquals("logged-in-token", token);
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentials() {
        String email = "login@example.com";
        UserEntity entity = new UserEntity();
        User user = new User();
        user.setPassword("hashed");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(email, "wrong"));
    }

    @Test
    void login_blockedUser_throwsAccountIsBlocked() {
        String email = "login@example.com";
        UserEntity entity = new UserEntity();
        User user = new User();
        user.setPassword("hashed");
        user.setStatus(Status.BLOCKED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(user);
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);

        assertThrows(AccountIsBlockedException.class, () -> authService.login(email, "pass"));
    }
}
