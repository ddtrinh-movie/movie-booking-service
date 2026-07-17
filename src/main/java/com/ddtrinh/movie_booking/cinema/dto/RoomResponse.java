package com.ddtrinh.movie_booking.cinema.dto;

import com.ddtrinh.movie_booking.cinema.entity.Room;

import java.util.UUID;

public class RoomResponse {

    private final UUID id;
    private final UUID cinemaId;
    private final String cinemaName;
    private final String name;
    private final Integer totalSeats;

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.cinemaId = room.getCinema().getId();
        this.cinemaName = room.getCinema().getName();
        this.name = room.getName();
        this.totalSeats = room.getTotalSeats();
    }
}
