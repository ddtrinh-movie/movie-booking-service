package com.ddtrinh.movie_booking.booking.scheduler;

import com.ddtrinh.movie_booking.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    private final BookingService bookingService;

    @Scheduled(fixedDelay = 60_000)
    public void expirePendingBookings() {
        int expiredCount = bookingService.expirePendingBookings();
        if (expiredCount > 0) {
            log.info("Expired {} PENDING booking(s) and released their seats", expiredCount);
        }
    }
}
