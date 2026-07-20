package com.ddtrinh.movie_booking.booking.service;

import com.ddtrinh.movie_booking.booking.dto.BookingRequest;
import com.ddtrinh.movie_booking.booking.dto.BookingResponse;
import com.ddtrinh.movie_booking.booking.entiy.Booking;
import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import com.ddtrinh.movie_booking.booking.entiy.BookingStatus;
import com.ddtrinh.movie_booking.booking.repository.BookingRepository;
import com.ddtrinh.movie_booking.booking.repository.BookingSeatRepository;
import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import com.ddtrinh.movie_booking.showtime.entiy.Showtime;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeatStatus;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeRepository;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeSeatRepository;
import com.ddtrinh.movie_booking.user.entiy.User;
import com.ddtrinh.movie_booking.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final Duration HOLD_DURATION = Duration.ofMinutes(10);

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeSeatRepository showtimeSeatRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse create(UUID userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Showtime not found with id: " + request.getShowtimeId()));

        if (showtime.isDeleted()) {
            throw new ConflictException("This showtime has been cancelled");
        }
        if (showtime.getStartTime().isBefore(Instant.now())) {
            throw new ConflictException("This showtime has already started");
        }

        List<ShowtimeSeat> showtimeSeats = showtimeSeatRepository
                .findAllByShowtimeIdAndIdInForUpdate(request.getShowtimeId(), request.getSeatIds());

        if (showtimeSeats.size() != request.getSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats do not belong to this showtime");
        }

        for (ShowtimeSeat showtimeSeat : showtimeSeats) {
            if (showtimeSeat.getStatus() != ShowtimeSeatStatus.AVAILABLE) {
                throw new ConflictException(
                        "Seat " + showtimeSeat.getSeat().getRowLabel() + showtimeSeat.getSeat().getSeatNumber()
                                + " is no longer available");
            }
        }

        BigDecimal total = showtime.getPrice().multiply(BigDecimal.valueOf(showtimeSeats.size()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setExpiresAt(Instant.now().plus(HOLD_DURATION));
        booking.setTotalAmount(total);
        bookingRepository.save(booking);

        for (ShowtimeSeat showtimeSeat : showtimeSeats) {
            showtimeSeat.setStatus(ShowtimeSeatStatus.BOOKED);
            showtimeSeatRepository.save(showtimeSeat);

            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setShowtimeSeat(showtimeSeat);
            bookingSeat.setPrice(showtime.getPrice());
            bookingSeatRepository.save(bookingSeat);
        }

        List<BookingSeat> savedBookingSeats = bookingSeatRepository.findAllByBookingId(booking.getId());
        return new BookingResponse(booking, savedBookingSeats);
    }

    public BookingResponse getById(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllByBookingId(id);
        return new BookingResponse(booking, bookingSeats);
    }
}
