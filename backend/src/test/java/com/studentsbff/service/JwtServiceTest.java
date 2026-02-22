package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.studentsbff.config.JwtProperties;
import com.studentsbff.model.Role;
import com.studentsbff.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtProperties jwtProperties;

    private static final String SECRET =
            "test-secret-key-for-testing-only-must-be-at-least-256-bits-long-to-satisfy-hmac-sha256";

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(SECRET);
        jwtProperties.setExpiration(3600000L);
        jwtService = new JwtService(jwtProperties);
    }

    private User createTestUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void shouldGenerateValidToken() {
        User user = createTestUser();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void shouldExtractUserIdFromToken() {
        User user = createTestUser();

        String token = jwtService.generateToken(user);

        UUID extractedId = jwtService.extractUserId(token);
        assertThat(extractedId).isEqualTo(user.getId());
    }

    @Test
    void shouldExtractEmailFromToken() {
        User user = createTestUser();

        String token = jwtService.generateToken(user);

        String extractedEmail = jwtService.extractEmail(token);
        assertThat(extractedEmail).isEqualTo("test@example.com");
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtProperties expiredProps = new JwtProperties();
        expiredProps.setSecret(SECRET);
        expiredProps.setExpiration(0L);
        JwtService expiredJwtService = new JwtService(expiredProps);

        User user = createTestUser();
        String token = expiredJwtService.generateToken(user);

        assertThat(expiredJwtService.isTokenValid(token)).isFalse();
    }

    @Test
    void shouldRejectTamperedToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        // Tamper with the token by changing a character in the signature
        String tamperedToken = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
    }
}
