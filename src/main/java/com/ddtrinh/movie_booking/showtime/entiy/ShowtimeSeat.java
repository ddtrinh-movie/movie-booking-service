package com.ddtrinh.movie_booking.showtime.entiy;

import com.ddtrinh.movie_booking.cinema.entity.Seat;
import com.ddtrinh.movie_booking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "showtime_seats")
public class ShowtimeSeat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowtimeSeatStatus status = ShowtimeSeatStatus.AVAILABLE;
}
