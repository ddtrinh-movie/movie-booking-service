package com.ddtrinh.movie_booking.showtime.entiy;

import com.ddtrinh.movie_booking.cinema.entity.Room;
import com.ddtrinh.movie_booking.common.base.BaseEntity;
import com.ddtrinh.movie_booking.movie.entiy.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "showtimes")
public class Showtime extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "audio_type", nullable = false)
    private AudioType audioType = AudioType.ORIGINAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "subtitle_type", nullable = false)
    private SubtitleType subtitleType = SubtitleType.NONE;

    @Column(nullable = false)
    private boolean deleted = false;
}
