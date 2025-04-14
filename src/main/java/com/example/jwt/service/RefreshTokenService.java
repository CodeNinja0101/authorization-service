package com.example.jwt.service;

import com.example.jwt.entity.RefreshToken;
import com.example.jwt.exception.InvalidRefreshTokenException;
import com.example.jwt.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createrefreshToken(String username) {
        refreshTokenRepository.deleteByUsername(username);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenValidity));
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException("Refresh token expired");
        }
        return refreshToken;
    }
}
