package com.ddtrinh.movie_booking.outbox;

import com.ddtrinh.movie_booking.outbox.entity.OutboxEvent;
import com.ddtrinh.movie_booking.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OutboxWriter {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void write(String aggregateType, String aggregateId, String exchange, String routingKey, Object event) {
        String payload;
        payload = objectMapper.writeValueAsString(event);
        outboxEventRepository.save(new OutboxEvent(aggregateType, aggregateId, exchange, routingKey, payload));
    }
}
