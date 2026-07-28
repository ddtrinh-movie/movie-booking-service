package com.ddtrinh.movie_booking.booking.service;

import com.ddtrinh.movie_booking.booking.dto.BookingResponse;
import com.ddtrinh.movie_booking.booking.entiy.Booking;
import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import com.ddtrinh.movie_booking.booking.entiy.BookingStatus;
import com.ddtrinh.movie_booking.booking.event.BookingCancelledEvent;
import com.ddtrinh.movie_booking.booking.repository.BookingRepository;
import com.ddtrinh.movie_booking.booking.repository.BookingSeatRepository;
import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import com.ddtrinh.movie_booking.outbox.OutboxWriter;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeatStatus;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeSeatRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingCancelWriter {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeSeatRepository showtimeSeatRepository;
    private final OutboxWriter outboxWriter;

    @Retry(name = "bookingStatusWrite")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse writeCancelled(UUID bookingId, BigDecimal refundAmount) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException(
                    "Only a confirmed booking can be cancelled, current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.saveAndFlush(booking);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllByBookingId(booking.getId());
        for (BookingSeat bookingSeat : bookingSeats) {
            ShowtimeSeat showtimeSeat = bookingSeat.getShowtimeSeat();
            showtimeSeat.setStatus(ShowtimeSeatStatus.AVAILABLE);
            showtimeSeatRepository.save(showtimeSeat);
        }

        outboxWriter.write(
                "Booking",
                booking.getId().toString(),
                RabbitMQConfig.BOOKING_EVENTS_EXCHANGE,
                RabbitMQConfig.BOOKING_CANCELLED_ROUTING_KEY,
                new BookingCancelledEvent(
                        booking.getId(),
                        booking.getUser().getEmail(),
                        booking.getShowtime().getMovie().getTitle(),
                        refundAmount,
                        Instant.now())
        );
        return new BookingResponse(saved, bookingSeats);
    }
}
