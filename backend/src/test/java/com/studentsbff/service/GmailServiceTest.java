package com.studentsbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.studentsbff.model.Role;
import com.studentsbff.model.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GmailServiceTest {

    @Mock
    private OAuth2TokenService oAuth2TokenService;

    @Test
    void shouldThrowWhenNoTokens() {
        GmailService gmailService = new GmailService(oAuth2TokenService, 50);
        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .email("test@example.com")
                        .name("Test")
                        .role(Role.STUDENT)
                        .build();

        assertThatThrownBy(() -> gmailService.fetchRecentEmails(user, 7))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No Gmail access token");
    }

    @Test
    void shouldNotThrowWhenTokenExists() {
        GmailService gmailService = new GmailService(oAuth2TokenService, 50);
        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .email("test@example.com")
                        .name("Test")
                        .role(Role.STUDENT)
                        .googleAccessToken("valid-token")
                        .googleTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS))
                        .build();

        // This will throw a RestClientException since Gmail API is not reachable in tests,
        // but it should NOT throw IllegalStateException (token validation passes)
        try {
            gmailService.fetchRecentEmails(user, 7);
        } catch (IllegalStateException e) {
            // This should not happen
            throw new AssertionError("Should not throw IllegalStateException when token exists", e);
        } catch (Exception e) {
            // Expected: network error since we're not mocking the REST call
            assertThat(e).isNotInstanceOf(IllegalStateException.class);
        }
    }
}
