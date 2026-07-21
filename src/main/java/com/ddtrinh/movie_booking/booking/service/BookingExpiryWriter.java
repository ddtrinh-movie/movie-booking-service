package com.ddtrinh.movie_booking.booking.service;

import com.ddtrinh.movie_booking.booking.entiy.Booking;
import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import com.ddtrinh.movie_booking.booking.entiy.BookingStatus;
import com.ddtrinh.movie_booking.booking.event.BookingExpiredEvent;
import com.ddtrinh.movie_booking.booking.repository.BookingRepository;
import com.ddtrinh.movie_booking.booking.repository.BookingSeatRepository;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeatStatus;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingExpiryWriter {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeSeatRepository showtimeSeatRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void expireAndPersist(Booking booking) {
        booking.setStatus(BookingStatus.EXPIRED);
        Booking managedBooking = bookingRepository.saveAndFlush(booking);

        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllByBookingId(booking.getId());
        for (BookingSeat bookingSeat : bookingSeats) {
            ShowtimeSeat showtimeSeat = bookingSeat.getShowtimeSeat();
            showtimeSeat.setStatus(ShowtimeSeatStatus.AVAILABLE);
            showtimeSeatRepository.save(showtimeSeat);
        }

        eventPublisher.publishEvent(new BookingExpiredEvent(
                managedBooking.getId(),
                managedBooking.getUser().getEmail(),
                managedBooking.getShowtime().getMovie().getTitle(),
                managedBooking.getShowtime().getStartTime()));
    }
}
