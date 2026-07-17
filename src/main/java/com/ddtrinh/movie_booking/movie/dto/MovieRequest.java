package com.ddtrinh.movie_booking.movie.dto;

import com.ddtrinh.movie_booking.movie.entiy.Genre;
import com.ddtrinh.movie_booking.movie.entiy.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MovieRequest {

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String description;

    @NotNull(message = "Duration must not be null")
    @Positive(message = "Duration must be a positive number")
    private Integer durationMinutes;

    @NotNull(message = "Genre must not be null")
    private Genre genre;

    private String posterUrl;

    private LocalDate releaseDate;

    @NotNull(message = "Status must not be null")
    private MovieStatus status;
}
