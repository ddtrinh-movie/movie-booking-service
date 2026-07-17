package com.ddtrinh.movie_booking.showtime.dto;

import com.ddtrinh.movie_booking.showtime.entiy.AudioType;
import com.ddtrinh.movie_booking.showtime.entiy.Showtime;
import com.ddtrinh.movie_booking.showtime.entiy.SubtitleType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ShowtimeResponse {

    private final UUID id;
    private final UUID movieId;
    private final String movieTitle;
    private final UUID roomId;
    private final String roomName;
    private final UUID cinemaId;
    private final String cinemaName;
    private final Instant startTime;
    private final Instant endTime;
    private final BigDecimal price;
    private final AudioType audioType;
    private final SubtitleType subtitleType;

    public ShowtimeResponse(Showtime showtime) {
        this.id = showtime.getId();
        this.movieId = showtime.getMovie().getId();
        this.movieTitle = showtime.getMovie().getTitle();
        this.roomId = showtime.getRoom().getId();
        this.roomName = showtime.getRoom().getName();
        this.cinemaId = showtime.getRoom().getCinema().getId();
        this.cinemaName = showtime.getRoom().getCinema().getName();
        this.startTime = showtime.getStartTime();
        this.endTime = showtime.getEndTime();
        this.price = showtime.getPrice();
        this.audioType = showtime.getAudioType();
        this.subtitleType = showtime.getSubtitleType();
    }
}
