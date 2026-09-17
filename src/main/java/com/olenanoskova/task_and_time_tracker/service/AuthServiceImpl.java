package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.exception.AccountIsBlockedException;
import com.olenanoskova.task_and_time_tracker.exception.InvalidCredentialsException;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.repository.UserRepository;
import com.olenanoskova.task_and_time_tracker.repository.entity.UserEntity;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import com.olenanoskova.task_and_time_tracker.service.model.Status;
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
    private final UserMapper userMapper;



    @Override
    public String signUp(User user) {

        log.info("Attempting to sign-up user with email {}", user.getEmail());

        // User createdUser = userService.createUser(user)
        //String token = tokenService.createToken(createdUser.getId().toString(), Role.WORKER);
        User createdUser = userService.createUser(user);
        String token = tokenService.createToken(createdUser.getId().toString(), Role.USER);

        log.info("Successfully created the user with email {}", user.getEmail());

        return token;
    }

    @Override
    public String loginUser(String email, String password) {

        log.info("Attempting to login user with email {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        User user = userMapper.toDomain(userEntity);

        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }
        if (user.getStatus() == Status.BLOCKED) {
            throw new AccountIsBlockedException();
        }

        String token = tokenService.createToken(user.getId().toString(), Role.USER);

        log.info("Successfully login the user with email {}", email);

        return  token;
    }


}
