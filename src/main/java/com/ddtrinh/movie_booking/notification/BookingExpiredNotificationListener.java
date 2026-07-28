package com.ddtrinh.movie_booking.notification;

import com.ddtrinh.movie_booking.booking.event.BookingExpiredEvent;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import com.ddtrinh.movie_booking.messaging.ProcessedEventGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiredNotificationListener {

    private final ProcessedEventGuard processedEventGuard;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_EXPIRED_NOTIFICATION_QUEUE)
    public void onBookingExpired(BookingExpiredEvent event,
                                 @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        UUID eventId = UUID.fromString(messageId);
        if (!processedEventGuard.markProcessed(eventId, "booking-expired-notification")) {
            log.info("Duplicate delivery for event {}, skip", eventId);
            return;
        }
        log.info("[simulated email] To: {} — your held seats for \"{}\" at {} were released "
                        + "because booking {} was not confirmed in time.",
                event.userEmail(), event.movieTitle(), event.showtimeStartTime(), event.bookingId());
    }
}
