package com.ddtrinh.movie_booking.outbox;

import com.ddtrinh.movie_booking.outbox.entity.OutboxEvent;
import com.ddtrinh.movie_booking.outbox.entity.OutboxStatus;
import com.ddtrinh.movie_booking.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final int BATCH_SIZE = 20;
    private static final int MAX_ATTEMPTS = 5;

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void publishPending() {
        List<OutboxEvent> batch = outboxEventRepository.findPendingBatchForUpdate(BATCH_SIZE);
        for (OutboxEvent event : batch) {
            publishOne(event);
        }
    }

    private void publishOne(OutboxEvent event) {
        try {
            Message message = MessageBuilder
                    .withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setMessageId(event.getId().toString())
                    .build();
            rabbitTemplate.send(event.getExchange(), event.getRoutingKey(), message);
            event.setStatus(OutboxStatus.SENT);
            event.setSentAt(Instant.now());
        } catch (AmqpException e) {
            event.setAttemptCount(event.getAttemptCount() + 1);
            if (event.getAttemptCount() >= MAX_ATTEMPTS) {
                event.setStatus(OutboxStatus.FAILED);
                log.error("Outbox event {} ({}) exceeded {} attempts, marking FAILED for manual review: {}",
                        event.getId(), event.getRoutingKey(), MAX_ATTEMPTS, e.getMessage());
            } else {
                log.warn("Failed to publish outbox event {} ({}), attempt {}/{}: {}",
                        event.getId(), event.getRoutingKey(), event.getAttemptCount(), MAX_ATTEMPTS, e.getMessage());
            }
        }
        outboxEventRepository.save(event);
    }
}
