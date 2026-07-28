package com.ddtrinh.movie_booking.notification;

import com.ddtrinh.movie_booking.booking.event.BookingConfirmedEvent;
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
public class BookingConfirmationNotificationListener {

    private final ProcessedEventGuard processedEventGuard;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_CONFIRMED_NOTIFICATION_QUEUE)
    public void onBookingConfirmed(BookingConfirmedEvent event,
                                   @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        UUID eventId = UUID.fromString(messageId);
        if (!processedEventGuard.markProcessed(eventId, "booking-confirmation-notification")) {
            log.info("Duplicate delivery for event {}, skip", eventId);
            return;
        }
        log.info("[simulated email] To: {} — your booking {} for \"{}\" at {} is confirmed. Total: {}",
                event.userEmail(), event.bookingId(), event.movieTitle(), event.showtimeStartTime(),
                event.totalAmount());
    }
}
