package com.ddtrinh.movie_booking.booking.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingConfirmedEvent(
        UUID bookingId,
        String userEmail,
        String movieTitle,
        Instant showtimeStartTime,
        BigDecimal totalAmount
) {
}
