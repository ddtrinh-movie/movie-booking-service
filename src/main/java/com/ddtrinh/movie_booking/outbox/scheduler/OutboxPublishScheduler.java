package com.ddtrinh.movie_booking.outbox.scheduler;

import com.ddtrinh.movie_booking.outbox.OutboxPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxPublishScheduler {

    private final OutboxPublisher outboxPublisher;
    @Scheduled(fixedDelay = 5_000)
    public void publishPendingOutboxEvents() {
        outboxPublisher.publishPending();
    }
}
