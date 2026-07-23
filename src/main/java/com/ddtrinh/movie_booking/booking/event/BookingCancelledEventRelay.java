package com.ddtrinh.movie_booking.booking.event;

import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCancelledEventRelay {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCancelled(BookingCancelledEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EVENTS_EXCHANGE,
                RabbitMQConfig.BOOKING_CANCELLED_ROUTING_KEY,
                event);
        log.info("Published BookingCancelledEvent for booking {}", event.bookingId());
    }
}
