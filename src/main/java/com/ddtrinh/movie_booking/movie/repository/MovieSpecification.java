package com.ddtrinh.movie_booking.movie.repository;

import com.ddtrinh.movie_booking.movie.entiy.Genre;
import com.ddtrinh.movie_booking.movie.entiy.Movie;
import com.ddtrinh.movie_booking.movie.entiy.MovieStatus;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecification {

    private MovieSpecification() {}

    public static Specification<Movie> excludeDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Movie> hasGenre(Genre genre) {
        return (root, query, cb) -> genre == null ? null : cb.equal(root.get("genre"), genre);
    }

    public static Specification<Movie> hasStatus(MovieStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Movie> titleContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
        };
    }
}
