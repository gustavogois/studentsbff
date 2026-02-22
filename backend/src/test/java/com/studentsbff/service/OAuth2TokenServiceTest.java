package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.studentsbff.model.Role;
import com.studentsbff.model.User;
import com.studentsbff.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuth2TokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OAuth2TokenService oAuth2TokenService;

    @Test
    void shouldPersistTokensForUser() {
        User user = User.builder().id(UUID.randomUUID()).email("test@example.com").name("Test").role(Role.STUDENT).build();
        Instant expiry = Instant.now().plus(1, ChronoUnit.HOURS);

        oAuth2TokenService.persistTokens(user, "access-token-123", "refresh-token-456", expiry);

        assertThat(user.getGoogleAccessToken()).isEqualTo("access-token-123");
        assertThat(user.getGoogleRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(user.getGoogleTokenExpiry()).isEqualTo(expiry);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateTokensOnReLogin() {
        User user = User.builder().id(UUID.randomUUID()).email("test@example.com").name("Test").role(Role.STUDENT).googleRefreshToken("old-refresh").build();
        Instant expiry = Instant.now().plus(1, ChronoUnit.HOURS);

        oAuth2TokenService.persistTokens(user, "new-access", null, expiry);

        assertThat(user.getGoogleAccessToken()).isEqualTo("new-access");
        assertThat(user.getGoogleRefreshToken()).isEqualTo("old-refresh");
        verify(userRepository).save(user);
    }

    @Test
    void shouldHandleNullRefreshToken() {
        User user = User.builder().id(UUID.randomUUID()).email("test@example.com").name("Test").role(Role.STUDENT).build();
        Instant expiry = Instant.now().plus(1, ChronoUnit.HOURS);

        oAuth2TokenService.persistTokens(user, "access-token", null, expiry);

        assertThat(user.getGoogleAccessToken()).isEqualTo("access-token");
        assertThat(user.getGoogleRefreshToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void shouldCheckTokenValidity() {
        User validUser = User.builder().id(UUID.randomUUID()).email("test@example.com").name("Test").role(Role.STUDENT)
                .googleAccessToken("token").googleTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS)).build();

        User expiredUser = User.builder().id(UUID.randomUUID()).email("test2@example.com").name("Test2").role(Role.STUDENT)
                .googleAccessToken("token").googleTokenExpiry(Instant.now().minus(1, ChronoUnit.HOURS)).build();

        User noTokenUser = User.builder().id(UUID.randomUUID()).email("test3@example.com").name("Test3").role(Role.STUDENT).build();

        assertThat(oAuth2TokenService.hasValidToken(validUser)).isTrue();
        assertThat(oAuth2TokenService.hasValidToken(expiredUser)).isFalse();
        assertThat(oAuth2TokenService.hasValidToken(noTokenUser)).isFalse();
    }
}
