package com.ddtrinh.movie_booking.compensation.entity;

import com.ddtrinh.movie_booking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "compensation_logs")
public class CompensationLog extends BaseEntity {

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CompensationType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private boolean resolved = false;
}
