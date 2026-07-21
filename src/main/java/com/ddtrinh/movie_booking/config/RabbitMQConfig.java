package com.ddtrinh.movie_booking.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_EVENTS_EXCHANGE = "booking-events";

    public static final String BOOKING_CONFIRMED_ROUTING_KEY = "booking.confirmed";
    public static final String BOOKING_CONFIRMED_NOTIFICATION_QUEUE = "notification.booking-confirmed";
    public static final String BOOKING_CONFIRMED_AUDIT_LOG_QUEUE = "audit-log.booking-confirmed";

    public static final String BOOKING_EXPIRED_ROUTING_KEY = "booking.expired";
    public static final String BOOKING_EXPIRED_NOTIFICATION_QUEUE = "notification.booking-expired";

    @Bean
    public TopicExchange bookingEventsExchange() {
        return new TopicExchange(BOOKING_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue bookingConfirmedNotificationQueue() {
        return new Queue(BOOKING_CONFIRMED_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding bookingConfirmedNotificationBinding(Queue bookingConfirmedNotificationQueue, TopicExchange bookingEventsExchange) {
        return BindingBuilder.bind(bookingConfirmedNotificationQueue)
                .to(bookingEventsExchange)
                .with(BOOKING_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Queue bookingConfirmedAuditLogQueue() {
        return new Queue(BOOKING_CONFIRMED_AUDIT_LOG_QUEUE, true);
    }

    @Bean
    public Binding bookingConfirmedAuditLogBinding(Queue bookingConfirmedAuditLogQueue, TopicExchange bookingEventsExchange) {
        return BindingBuilder.bind(bookingConfirmedAuditLogQueue)
                .to(bookingEventsExchange)
                .with(BOOKING_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Queue bookingExpiredNotificationQueue() {
        return new Queue(BOOKING_EXPIRED_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding bookingExpiredNotificationBinding(Queue bookingExpiredNotificationQueue, TopicExchange bookingEventsExchange) {
        return BindingBuilder.bind(bookingExpiredNotificationQueue)
                .to(bookingEventsExchange)
                .with(BOOKING_EXPIRED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
