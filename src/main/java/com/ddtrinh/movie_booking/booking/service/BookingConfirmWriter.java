package com.ddtrinh.movie_booking.booking.service;

import com.ddtrinh.movie_booking.booking.dto.BookingResponse;
import com.ddtrinh.movie_booking.booking.entiy.Booking;
import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import com.ddtrinh.movie_booking.booking.entiy.BookingStatus;
import com.ddtrinh.movie_booking.booking.event.BookingConfirmedEvent;
import com.ddtrinh.movie_booking.booking.repository.BookingRepository;
import com.ddtrinh.movie_booking.booking.repository.BookingSeatRepository;
import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import com.ddtrinh.movie_booking.config.RabbitMQConfig;
import com.ddtrinh.movie_booking.outbox.OutboxWriter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingConfirmWriter {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final OutboxWriter outboxWriter;

    @Retry(name = "bookingStatusWrite")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse writeConfirmed(UUID bookingId, UUID paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException(
                    "Booking is no longer confirmable, current status: " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);
        Booking saved = bookingRepository.saveAndFlush(booking);

        outboxWriter.write(
                "Booking",
                saved.getId().toString(),
                RabbitMQConfig.BOOKING_EVENTS_EXCHANGE,
                RabbitMQConfig.BOOKING_CONFIRMED_ROUTING_KEY,
                new BookingConfirmedEvent(
                        saved.getId(),
                        saved.getUser().getEmail(),
                        saved.getShowtime().getMovie().getTitle(),
                        saved.getShowtime().getStartTime(),
                        saved.getTotalAmount())
                );
        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllByBookingId(saved.getId());
        return new BookingResponse(saved, bookingSeats);
    }
}