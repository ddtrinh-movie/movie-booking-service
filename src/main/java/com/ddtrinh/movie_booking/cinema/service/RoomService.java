package com.ddtrinh.movie_booking.cinema.service;

import com.ddtrinh.movie_booking.cinema.dto.RoomRequest;
import com.ddtrinh.movie_booking.cinema.dto.RoomResponse;
import com.ddtrinh.movie_booking.cinema.dto.RowLayoutRequest;
import com.ddtrinh.movie_booking.cinema.dto.SeatResponse;
import com.ddtrinh.movie_booking.cinema.entity.Cinema;
import com.ddtrinh.movie_booking.cinema.entity.Room;
import com.ddtrinh.movie_booking.cinema.entity.Seat;
import com.ddtrinh.movie_booking.cinema.entity.SeatType;
import com.ddtrinh.movie_booking.cinema.repository.RoomRepository;
import com.ddtrinh.movie_booking.cinema.repository.SeatRepository;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final CinemaService cinemaService;

    public List<RoomResponse> getAllByCinema(UUID cinemaId) {
        return roomRepository.findAllByCinemaId(cinemaId).stream().map(RoomResponse::new).toList();
    }

    public RoomResponse getById(UUID id) {
        return new RoomResponse(findRoomOrThrow(id));
    }

    public List<SeatResponse> getSeats(UUID roomId) {
        findRoomOrThrow(roomId);
        return seatRepository.findAllByRoomId(roomId).stream().map(SeatResponse::new).toList();
    }

    @Transactional
    public RoomResponse create(RoomRequest request) {
        Cinema cinema = cinemaService.findCinemaOrThrow(request.getCinemaId());

        Room room = new Room();
        room.setCinema(cinema);
        room.setName(request.getName());
        int totalSeats = request.getRows().stream().mapToInt(RowLayoutRequest::getSeatCount).sum();
        room.setTotalSeats(totalSeats);
        roomRepository.save(room);

        for (RowLayoutRequest row : request.getRows()) {

            Map<Integer, SeatType> seatTypeBySeatNumber = new HashMap<>();
            if (row.getSeatTypeOverrides() != null) {
                for (Map.Entry<SeatType, List<Integer>> entry : row.getSeatTypeOverrides().entrySet()) {
                    for (Integer seatNumber : entry.getValue()) {
                        seatTypeBySeatNumber.put(seatNumber, entry.getKey());
                    }
                }
            }

            for (int seatNumber = 1; seatNumber <= row.getSeatCount(); seatNumber++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setRowLabel(row.getRowLabel());
                seat.setSeatNumber(seatNumber);
                seat.setSeatType(seatTypeBySeatNumber.getOrDefault(seatNumber, SeatType.STANDARD));
                seatRepository.save(seat);
            }
        }

        return new RoomResponse(room);
    }

    Room findRoomOrThrow(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }
}
