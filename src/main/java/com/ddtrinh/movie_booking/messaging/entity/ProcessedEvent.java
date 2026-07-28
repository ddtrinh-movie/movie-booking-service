package com.ddtrinh.movie_booking.messaging.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
@IdClass(ProcessedEventId.class)
public class ProcessedEvent {

    @Id
    private UUID eventId;

    @Id
    private String consumerName;

    @Column(nullable = false)
    private Instant processedAt = Instant.now();

    public ProcessedEvent() {

    }

    public ProcessedEvent(UUID eventId, String consumerName, Instant processedAt) {
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.processedAt = processedAt;
    }
}

