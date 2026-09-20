package com.olenanoskova.task_and_time_tracker.controller;

import com.olenanoskova.task_and_time_tracker.controller.dto.*;
import com.olenanoskova.task_and_time_tracker.mapper.UserMapper;
import com.olenanoskova.task_and_time_tracker.service.AuthService;
import com.olenanoskova.task_and_time_tracker.service.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/sign-up/personal")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponseDto> signUpPersonal(
            @Valid @RequestBody RegisterUserRequestDto request) {

        log.info("Received personal sign-up request for {}", request.getEmail());
        String token = authService.signUpPersonalUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @PostMapping("/auth/sign-up/company")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponseDto> signUpCompany(
            @Valid @RequestBody RegisterCompanyRequestDto request) {

        log.info("Received company sign-up request for {}", request.getEmail());
        String token = authService.signUpCompanyUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @PostMapping("/auth/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        log.info("Received login request for {}", request.getEmail());
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
