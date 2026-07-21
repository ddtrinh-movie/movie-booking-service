package com.ddtrinh.movie_booking.notification;

import com.ddtrinh.movie_booking.booking.event.BookingExpiredEvent;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingExpiredNotificationListener {

    @RabbitListener(queues = RabbitMQConfig.BOOKING_EXPIRED_NOTIFICATION_QUEUE)
    public void onBookingExpired(BookingExpiredEvent event) {
        log.info("[simulated email] To: {} — your held seats for \"{}\" at {} were released "
                        + "because booking {} was not confirmed in time.",
                event.userEmail(), event.movieTitle(), event.showtimeStartTime(), event.bookingId());
    }
}
