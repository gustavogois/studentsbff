package com.studentsbff.config;

import com.studentsbff.model.User;
import com.studentsbff.service.JwtService;
import com.studentsbff.service.OAuth2TokenService;
import com.studentsbff.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final OAuth2TokenService oAuth2TokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            UserService userService,
            JwtService jwtService,
            OAuth2TokenService oAuth2TokenService,
            OAuth2AuthorizedClientService authorizedClientService,
            @Value("${frontend.url}") String frontendUrl) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.oAuth2TokenService = oAuth2TokenService;
        this.authorizedClientService = authorizedClientService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        User user = userService.findOrCreateOAuthUser(email, name, picture);

        persistGoogleTokens(authentication, user);

        String jwt = jwtService.generateToken(user);

        response.sendRedirect(frontendUrl + "/oauth/callback?token=" + jwt);
    }

    private void persistGoogleTokens(Authentication authentication, User user) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient client =
                    authorizedClientService.loadAuthorizedClient(
                            oauthToken.getAuthorizedClientRegistrationId(),
                            oauthToken.getName());

            if (client != null) {
                OAuth2AccessToken accessToken = client.getAccessToken();
                OAuth2RefreshToken refreshToken = client.getRefreshToken();

                String accessTokenValue = accessToken.getTokenValue();
                String refreshTokenValue =
                        refreshToken != null ? refreshToken.getTokenValue() : null;
                Instant expiry = accessToken.getExpiresAt();

                oAuth2TokenService.persistTokens(
                        user, accessTokenValue, refreshTokenValue, expiry);
            }
        }
    }
}
