package com.ddtrinh.movie_booking.audit;

import com.ddtrinh.movie_booking.booking.event.BookingConfirmedEvent;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import com.ddtrinh.movie_booking.messaging.ProcessedEventGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingConfirmedAuditLogListener {

    private final ProcessedEventGuard processedEventGuard;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_CONFIRMED_AUDIT_LOG_QUEUE)
    public void onBookingConfirmed(BookingConfirmedEvent event,
                                   @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        UUID eventId = UUID.fromString(messageId);
        if (!processedEventGuard.markProcessed(eventId, "booking-confirmed-audit-log")) {
            log.info("Duplicate delivery for event {}, skip", eventId);
            return;
        }
        log.info("[audit-log] booking={} user={} amount={} confirmedEventReceivedAt={}",
                event.bookingId(), event.userEmail(), event.totalAmount(), Instant.now());

    }
}
