package com.ddtrinh.movie_booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Bật JPA Auditing để tự động điền createdAt/updatedAt trong BaseEntity.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
