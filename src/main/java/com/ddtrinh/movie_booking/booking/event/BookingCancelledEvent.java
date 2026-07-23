package com.ddtrinh.movie_booking.booking.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingCancelledEvent(
        UUID bookingId,
        String userEmail,
        String movieTitle,
        BigDecimal refundAmount,
        Instant cancelledAt
) {
}
