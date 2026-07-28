package com.ddtrinh.movie_booking.messaging;

import com.ddtrinh.movie_booking.messaging.entity.ProcessedEvent;
import com.ddtrinh.movie_booking.messaging.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessedEventGuard {

    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public boolean markProcessed(UUID eventId, String consumerName) {
        try {
            processedEventRepository.saveAndFlush(new ProcessedEvent(eventId, consumerName, Instant.now()));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
