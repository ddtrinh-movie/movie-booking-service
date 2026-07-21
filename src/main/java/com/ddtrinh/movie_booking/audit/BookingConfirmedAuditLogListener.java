package com.ddtrinh.movie_booking.audit;

import com.ddtrinh.movie_booking.booking.event.BookingConfirmedEvent;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class BookingConfirmedAuditLogListener {

    @RabbitListener(queues = RabbitMQConfig.BOOKING_CONFIRMED_AUDIT_LOG_QUEUE)
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        log.info("[audit-log] booking={} user={} amount={} confirmedEventReceivedAt={}",
                event.bookingId(), event.userEmail(), event.totalAmount(), Instant.now());

    }
}
