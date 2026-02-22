package com.studentsbff.service;

import com.studentsbff.model.User;
import com.studentsbff.repository.UserRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2TokenService {

    private final UserRepository userRepository;

    public OAuth2TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void persistTokens(
            User user, String accessToken, String refreshToken, Instant expiry) {
        user.setGoogleAccessToken(accessToken);
        if (refreshToken != null) {
            user.setGoogleRefreshToken(refreshToken);
        }
        user.setGoogleTokenExpiry(expiry);
        userRepository.save(user);
    }

    public boolean hasValidToken(User user) {
        return user.getGoogleAccessToken() != null
                && user.getGoogleTokenExpiry() != null
                && user.getGoogleTokenExpiry().isAfter(Instant.now());
    }
}
