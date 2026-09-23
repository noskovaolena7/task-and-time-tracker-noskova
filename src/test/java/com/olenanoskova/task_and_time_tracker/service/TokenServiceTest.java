package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
        // Secret must be at least 256 bits (32 bytes) for HMAC-SHA256
        ReflectionTestUtils.setField(tokenService, "jwtSecret", "mySuperSecretKeyForJwtAuthenticationMustBeLongEnough123456");
        ReflectionTestUtils.setField(tokenService, "jwtTtlMillis", 3600000L);
    }

    @Test
    void createToken_and_validate() {
        String userId = "11111111-1111-1111-1111-111111111111";
        Role role = Role.ADMIN;

        String token = tokenService.createToken(userId, role);
        assertNotNull(token);
        assertTrue(tokenService.isValidToken(token));
        assertEquals(userId, tokenService.getUserId(token));
        assertEquals(role, tokenService.getRole(token));
    }

    @Test
    void isValidToken_returnsFalseForInvalid() {
        assertFalse(tokenService.isValidToken("invalid.token.here"));
    }
}
