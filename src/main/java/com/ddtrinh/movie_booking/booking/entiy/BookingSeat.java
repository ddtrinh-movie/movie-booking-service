package com.ddtrinh.movie_booking.booking.entiy;

import com.ddtrinh.movie_booking.common.base.BaseEntity;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "booking_seats")
public class BookingSeat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_seat_id", nullable = false)
    private ShowtimeSeat showtimeSeat;

    @Column(nullable = false)
    private BigDecimal price;
}
