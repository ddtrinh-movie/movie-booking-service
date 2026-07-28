package com.ddtrinh.movie_booking.outbox.entity;

import com.ddtrinh.movie_booking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "outbox_events")
public class OutboxEvent extends BaseEntity {

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(nullable = false, length = 100)
    private String exchange;

    @Column(name = "routing_key", nullable = false, length = 100)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name = "sent_at")
    private Instant sentAt;

    public OutboxEvent(String aggregateType, String aggregateId, String exchange, String routingKey, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.payload = payload;
    }
}
