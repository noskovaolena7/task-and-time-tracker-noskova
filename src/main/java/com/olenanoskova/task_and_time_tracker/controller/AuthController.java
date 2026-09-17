package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.LoginRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.SignUpRequestDto;
import com.olenanoskova.task_and_time_tracker.controller.dto.TokenResponseDto;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.service.AuthService;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {

        User user = userMapper.toDomain(signUpRequestDto);

        String token = authService.signUp(user);

        TokenResponseDto tokenResponseDto = new TokenResponseDto(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        String token = authService.loginUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        TokenResponseDto tokenResponseDto = new TokenResponseDto(token);

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDto);
    }
}
