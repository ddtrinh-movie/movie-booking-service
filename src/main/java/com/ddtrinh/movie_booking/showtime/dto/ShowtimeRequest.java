package com.ddtrinh.movie_booking.showtime.dto;

import com.ddtrinh.movie_booking.showtime.entiy.AudioType;
import com.ddtrinh.movie_booking.showtime.entiy.SubtitleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ShowtimeRequest {

    @NotNull(message = "movieId must not be null")
    private UUID movieId;

    @NotNull(message = "roomId must not be null")
    private UUID roomId;

    @NotNull(message = "startTime must not be null")
    private Instant startTime;

    @NotNull(message = "endTime must not be null")
    private Instant endTime;

    @NotNull(message = "price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "audioType must not be null")
    private AudioType audioType;

    @NotNull(message = "subtitleType must not be null")
    private SubtitleType subtitleType;
}
