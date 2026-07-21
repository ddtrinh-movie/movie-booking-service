package com.ddtrinh.movie_booking.notification;

import com.ddtrinh.movie_booking.booking.event.BookingConfirmedEvent;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingConfirmationNotificationListener {

    @RabbitListener(queues = RabbitMQConfig.BOOKING_CONFIRMED_NOTIFICATION_QUEUE)
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        log.info("[simulated email] To: {} — your booking {} for \"{}\" at {} is confirmed. Total: {}",
                event.userEmail(), event.bookingId(), event.movieTitle(), event.showtimeStartTime(),
                event.totalAmount());
    }
}
