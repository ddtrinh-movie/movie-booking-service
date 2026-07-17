package com.ddtrinh.movie_booking.showtime.repository;

import com.ddtrinh.movie_booking.showtime.entiy.AudioType;
import com.ddtrinh.movie_booking.showtime.entiy.Showtime;
import com.ddtrinh.movie_booking.showtime.entiy.SubtitleType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public class ShowtimeSpecification {
    private ShowtimeSpecification() {}

    public static Specification<Showtime> excludeCancelled() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Showtime> hasMovieId(UUID movieId) {
        return (root, query, cb) -> movieId == null ? null : cb.equal(root.get("movie").get("id"), movieId);
    }

    public static Specification<Showtime> hasCinemaId(UUID cinemaId) {
        return (root, query, cb) -> cinemaId == null ? null : cb.equal(root.get("cinema").get("id"), cinemaId);
    }

    public static Specification<Showtime> hasAudioType(AudioType audioType) {
        return (root, query, cb) -> audioType == null ? null : cb.equal(root.get("audioType"), audioType);
    }

    public static Specification<Showtime> hasSubtitleType(SubtitleType subtitleType) {
        return (root, query, cb) -> subtitleType == null ? null : cb.equal(root.get("subtitleType"), subtitleType);
    }

    public static Specification<Showtime> startsBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("startTime"), from, to);
            }
            return from != null
                    ? cb.greaterThanOrEqualTo(root.get("startTime"), from)
                    : cb.lessThanOrEqualTo(root.get("startTime"), to);
        };
    }
}
