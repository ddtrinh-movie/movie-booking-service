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
public class BookingConfirmedEventRelay {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EVENTS_EXCHANGE,
                RabbitMQConfig.BOOKING_CONFIRMED_ROUTING_KEY,
                event);
        log.info("Published BookingConfirmedEvent for booking {}", event.bookingId());
    }
}
