package com.ddtrinh.movie_booking.user.repository;

import com.ddtrinh.movie_booking.user.entiy.RefreshToken;
import com.ddtrinh.movie_booking.user.entiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    /**
     * Lấy toàn bộ refresh token còn hiệu lực của 1 user — dùng cho tính năng
     * multi-device (liệt kê thiết bị đang đăng nhập)
     */
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
}
