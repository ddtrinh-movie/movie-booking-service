package com.ddtrinh.movie_booking.security.service;

import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.UnauthorizedException;
import com.ddtrinh.movie_booking.security.dto.AuthResponse;
import com.ddtrinh.movie_booking.security.dto.LoginRequest;
import com.ddtrinh.movie_booking.security.dto.RefreshRequest;
import com.ddtrinh.movie_booking.security.dto.RegisterRequest;
import com.ddtrinh.movie_booking.security.jwt.JwtProvider;
import com.ddtrinh.movie_booking.user.entiy.RefreshToken;
import com.ddtrinh.movie_booking.user.entiy.User;
import com.ddtrinh.movie_booking.user.entiy.UserStatus;
import com.ddtrinh.movie_booking.user.repository.RefreshTokenRepository;
import com.ddtrinh.movie_booking.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw  new ConflictException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw  new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw  new UnauthorizedException("Account is locked");
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (oldToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (oldToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        return issueTokens(oldToken.getUser());
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshTokenValue = jwtProvider.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProvider.getRefreshTokenExpirationMs()));
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.of(accessToken, refreshTokenValue, jwtProvider.getAccessTokenExpirationMs());
    }
}
