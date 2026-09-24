package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterCompanyRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.RegisterUserRequestDto;
import com.olenanoskova.task_and_time_tracker.exception.AccountIsBlockedException;
import com.olenanoskova.task_and_time_tracker.exception.InvalidCredentialsException;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.repository.entity.WorkspaceEntity;
import com.olenanoskova.task_and_time_tracker.service.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CompanyService companyService;
    private final UserCompanyRoleService userCompanyRoleService;
    private final UserMapper userMapper;
    private final WorkspaceService workspaceService;


    @Override
    public String signUpPersonalUser(RegisterUserRequestDto request) {

        log.info("Attempting to register personal user with email {}", request.getEmail());

        User user = userMapper.toDomain(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.PERSONAL_USER);

        User createdUser = userService.createUser(user);
        WorkspaceEntity ws = workspaceService.createPersonalWorkspace(createdUser.getId());
        createdUser.setWorkspaceId(ws.getId());
        userService.updateUserWorkspace(createdUser.getId(), ws.getId());
        String token = tokenService.createToken(createdUser.getId().toString(), createdUser.getRole());

        log.info("Successfully registered personal user with email {}", createdUser.getEmail());
        return token;
    }

    @Override
    public String signUpCompanyUser(RegisterCompanyRequestDto request) {

        log.info("Attempting to register company user with email {}", request.getEmail());

        User user = userMapper.toDomain(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.COMPANY_USER);

        User createdUser = userService.createUser(user);

        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setDescription(request.getCompanyDescription());
        company.setOwnerId(createdUser.getId());

        Company createdCompany = companyService.createCompany(company);

        WorkspaceEntity ws = workspaceService.createCompanyWorkspace(createdUser.getId(), createdCompany.getId());
        createdUser.setWorkspaceId(ws.getId());
        userService.updateUserWorkspace(createdUser.getId(), ws.getId());

        UserCompanyRole ownerRole = new UserCompanyRole();
        ownerRole.setUserId(createdUser.getId());
        ownerRole.setCompanyId(createdCompany.getId());
        ownerRole.setRole(MemberRole.OWNER);

        userCompanyRoleService.assignRole(createdCompany.getId(), ownerRole);
        userService.updateRole(createdUser.getId(), Role.OWNER);

        String token = tokenService.createToken(createdUser.getId().toString(), Role.OWNER);

        log.info("Successfully registered company user {} and created company {}",
                createdUser.getEmail(), createdCompany.getName());

        return token;
    }

    @Override
    public String login(String email, String password) {

        String normalized = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
        log.info("Attempting to login user with email {}", normalized);

        UserEntity userEntity = userRepository.findByEmail(normalized)
                .orElseThrow(InvalidCredentialsException::new);

        User user = userMapper.toDomain(userEntity);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() == Status.BLOCKED) {
            throw new AccountIsBlockedException();
        }

        String token = tokenService.createToken(
                user.getId().toString(),
                user.getRole()
        );

        log.info("Successfully logged in user {}", email);
        return token;
    }

}
