package com.ddtrinh.movie_booking.cinema.dto;

import com.ddtrinh.movie_booking.cinema.entity.Seat;
import com.ddtrinh.movie_booking.cinema.entity.SeatType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SeatResponse {

    private final UUID id;
    private final String rowLabel;
    private final Integer seatNumber;
    private final SeatType seatType;

    public SeatResponse(Seat seat) {
        this.id = seat.getId();
        this.rowLabel = seat.getRowLabel();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
    }
}
