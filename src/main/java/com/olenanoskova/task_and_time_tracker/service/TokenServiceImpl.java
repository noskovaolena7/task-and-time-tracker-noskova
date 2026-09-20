package com.olenanoskova.task_and_time_tracker.service;



import com.olenanoskova.task_and_time_tracker.exception.InvalidCredentialsException;
import com.olenanoskova.task_and_time_tracker.service.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final String CLAIM_ROLE = "role";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.ttl-millis}")
    private Long jwtTtlMillis;

    @Override
    public String createToken(String userId, Role role) {

        // Calculate the expiration date based on the current time and expiration time in milliseconds
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtTtlMillis);

        // Build JWT claims
        Claims claims = Jwts.claims()
                        .issuedAt(now)
                        .expiration(expiration)
                        .subject(userId)
                        .add(CLAIM_ROLE, role.name())
                        .build();

        // Create and sign the JWT token
        return Jwts.builder().claims(claims).signWith(getSecretKey()).compact();
    }

    @Override
    public boolean isValidToken(String token) {

        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserId(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public Role getRole(String token) {

        String typeValue =
                Jwts.parser()
                        .verifyWith(getSecretKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .get(CLAIM_ROLE, String.class);

        try {
            return Role.valueOf(typeValue);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role value in token: {}", typeValue);
            throw new InvalidCredentialsException();
        }
    }

    private SecretKey getSecretKey() {

        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
