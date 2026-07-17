package com.ddtrinh.movie_booking.user.entiy;

import com.ddtrinh.movie_booking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    /**
     * Marks the token as revoked (logout, token rotation).
     * Used instead of hard-deleting the record, to keep history for audit/multi-device purposes.
     */
    @Column(nullable = false)
    private boolean revoked = false;
}
