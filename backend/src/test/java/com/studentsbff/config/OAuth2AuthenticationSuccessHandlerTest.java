package com.studentsbff.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.studentsbff.model.Role;
import com.studentsbff.model.User;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.OAuth2TokenService;
import com.studentsbff.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private OAuth2TokenService oAuth2TokenService;

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Mock
    private Authentication authentication;

    private OAuth2AuthenticationSuccessHandler handler;
    private static final String FRONTEND_URL = "http://localhost:5173";

    @BeforeEach
    void setUp() {
        handler =
                new OAuth2AuthenticationSuccessHandler(
                        userService,
                        jwtService,
                        oAuth2TokenService,
                        authorizedClientService,
                        FRONTEND_URL);
    }

    private User createTestUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .role(Role.STUDENT)
                .avatarUrl("https://example.com/avatar.jpg")
                .build();
    }

    @Test
    void shouldCreateUserOnFirstLogin() throws Exception {
        OAuth2User oAuth2User =
                new DefaultOAuth2User(
                        java.util.Collections.emptyList(),
                        Map.of(
                                "email", "new@example.com",
                                "name", "New User",
                                "picture", "https://example.com/pic.jpg",
                                "sub", "12345"),
                        "sub");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        User user = createTestUser();
        when(userService.findOrCreateOAuthUser(
                        "new@example.com", "New User", "https://example.com/pic.jpg"))
                .thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userService)
                .findOrCreateOAuthUser(
                        "new@example.com", "New User", "https://example.com/pic.jpg");
        assertThat(response.getRedirectedUrl())
                .isEqualTo(FRONTEND_URL + "/oauth/callback?token=jwt-token");
    }

    @Test
    void shouldReuseExistingUser() throws Exception {
        OAuth2User oAuth2User =
                new DefaultOAuth2User(
                        java.util.Collections.emptyList(),
                        Map.of(
                                "email", "existing@example.com",
                                "name", "Existing User",
                                "picture", "https://example.com/avatar.jpg",
                                "sub", "67890"),
                        "sub");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        User existingUser = createTestUser();
        when(userService.findOrCreateOAuthUser(
                        "existing@example.com",
                        "Existing User",
                        "https://example.com/avatar.jpg"))
                .thenReturn(existingUser);
        when(jwtService.generateToken(existingUser)).thenReturn("jwt-token-existing");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userService)
                .findOrCreateOAuthUser(
                        "existing@example.com",
                        "Existing User",
                        "https://example.com/avatar.jpg");
    }

    @Test
    void shouldRedirectWithJwtToken() throws Exception {
        OAuth2User oAuth2User =
                new DefaultOAuth2User(
                        java.util.Collections.emptyList(),
                        Map.of(
                                "email", "test@example.com",
                                "name", "Test User",
                                "picture", "https://example.com/pic.jpg",
                                "sub", "11111"),
                        "sub");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        User user = createTestUser();
        when(userService.findOrCreateOAuthUser(any(), any(), any())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("my-jwt-token");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getRedirectedUrl())
                .startsWith(FRONTEND_URL + "/oauth/callback?token=")
                .contains("my-jwt-token");
    }
}
