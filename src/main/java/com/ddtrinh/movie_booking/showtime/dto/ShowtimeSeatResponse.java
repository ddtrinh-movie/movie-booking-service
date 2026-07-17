package com.ddtrinh.movie_booking.showtime.dto;

import com.ddtrinh.movie_booking.cinema.entity.SeatType;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeatStatus;

import java.util.UUID;

public class ShowtimeSeatResponse {

    private final UUID id;
    private final String rowLabel;
    private final Integer seatNumber;
    private final SeatType seatType;
    private final ShowtimeSeatStatus status;

    public ShowtimeSeatResponse(ShowtimeSeat showtimeSeat) {
        this.id = showtimeSeat.getId();
        this.rowLabel = showtimeSeat.getSeat().getRowLabel();
        this.seatNumber = showtimeSeat.getSeat().getSeatNumber();
        this.seatType = showtimeSeat.getSeat().getSeatType();
        this.status = showtimeSeat.getStatus();
    }
}
