package com.ddtrinh.movie_booking.outbox.scheduler;

import com.ddtrinh.movie_booking.outbox.OutboxPublisher;
import com.ddtrinh.movie_booking.messaging.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class OutboxPublishScheduler {

    private final OutboxPublisher outboxPublisher;
    private final ProcessedEventRepository processedEventRepository;

    @Scheduled(fixedDelay = 5_000)
    public void publishPendingOutboxEvents() {
        outboxPublisher.publishPending();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldProcessedEvents() {
        processedEventRepository.deleteAllByProcessedAtBefore(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
