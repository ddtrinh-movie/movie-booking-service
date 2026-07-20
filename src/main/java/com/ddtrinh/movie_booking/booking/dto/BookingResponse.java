package com.ddtrinh.movie_booking.booking.dto;

import com.ddtrinh.movie_booking.booking.entiy.Booking;
import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import com.ddtrinh.movie_booking.booking.entiy.BookingStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class BookingResponse {

    private final UUID id;
    private final UUID showtimeId;
    private final String movieTitle;
    private final Instant showtimeStartTime;
    private final List<SeatInfo> seats;
    private final BigDecimal totalAmount;
    private final BookingStatus status;
    private final Instant expiresAt;

    public BookingResponse(Booking booking, List<BookingSeat> bookingSeats) {
        this.id = booking.getId();
        this.showtimeId = booking.getShowtime().getId();
        this.movieTitle = booking.getShowtime().getMovie().getTitle();
        this.showtimeStartTime = booking.getShowtime().getStartTime();
        this.seats = bookingSeats.stream().map(SeatInfo::new).toList();
        this.totalAmount = booking.getTotalAmount();
        this.status = booking.getStatus();
        this.expiresAt = booking.getExpiresAt();
    }

    @Getter
    public static class SeatInfo {
        private final String rowLabel;
        private final Integer seatNumber;
        private final BigDecimal price;

        public SeatInfo(BookingSeat bookingSeat) {
            this.rowLabel = bookingSeat.getShowtimeSeat().getSeat().getRowLabel();
            this.seatNumber = bookingSeat.getShowtimeSeat().getSeat().getSeatNumber();
            this.price = bookingSeat.getPrice();
        }
    }
}
