package com.ddtrinh.movie_booking.booking.event;

import java.time.Instant;
import java.util.UUID;

public record BookingExpiredEvent(
        UUID bookingId,
        String userEmail,
        String movieTitle,
        Instant showtimeStartTime
) {
}
