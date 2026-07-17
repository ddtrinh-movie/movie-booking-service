package com.ddtrinh.movie_booking.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn; // seconds

    public static AuthResponse of(String accessToken, String refreshToken, long accessTokenExpirationMs) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenExpirationMs / 1000);
    }
}
