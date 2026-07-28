package com.ddtrinh.movie_booking.booking.service;

import com.ddtrinh.movie_booking.booking.dto.BookingRequest;
import com.ddtrinh.movie_booking.booking.dto.BookingResponse;
import com.ddtrinh.movie_booking.booking.entiy.Booking;
import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import com.ddtrinh.movie_booking.booking.entiy.BookingStatus;
import com.ddtrinh.movie_booking.booking.repository.BookingRepository;
import com.ddtrinh.movie_booking.booking.repository.BookingSeatRepository;
import com.ddtrinh.movie_booking.common.exception.*;
import com.ddtrinh.movie_booking.compensation.CompensationLogger;
import com.ddtrinh.movie_booking.compensation.entity.CompensationType;
import com.ddtrinh.movie_booking.payment.client.PaymentClient;
import com.ddtrinh.movie_booking.payment.dto.PaymentChargeResponse;
import com.ddtrinh.movie_booking.payment.dto.PaymentStatus;
import com.ddtrinh.movie_booking.payment.dto.RefundChargeResponse;
import com.ddtrinh.movie_booking.showtime.entiy.Showtime;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeatStatus;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeRepository;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeSeatRepository;
import com.ddtrinh.movie_booking.user.entiy.User;
import com.ddtrinh.movie_booking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

    private final BookingExpiryWriter bookingExpiryWriter;
    private final BookingConfirmWriter bookingConfirmWriter;
    private final BookingCancelWriter bookingCancelWriter;

    private final PaymentClient paymentClient;
    private final CompensationLogger compensationLogger;


    @Transactional
    public BookingResponse create(UUID userId, BookingRequest request) {
        User user = findUserOrThrow(userId);
        Showtime showtime = findBookableShowtimeOrThrow(request.getShowtimeId());

        List<ShowtimeSeat> showtimeSeats = showtimeSeatRepository
                .findAllByShowtimeIdAndIdInForUpdate(request.getShowtimeId(), request.getSeatIds());
        if (showtimeSeats.size() != request.getSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats do not belong to this showtime");
        }
        validateAllAvailable(showtimeSeats);

        Booking booking = createPendingBooking(user, showtime, showtimeSeats.size());

        for (ShowtimeSeat showtimeSeat : showtimeSeats) {
            showtimeSeat.setStatus(ShowtimeSeatStatus.BOOKED);
            showtimeSeatRepository.save(showtimeSeat);
            saveBookingSeat(booking, showtimeSeat, showtime.getPrice());
        }

        return buildResponse(booking);
    }

    @Transactional
    public BookingResponse createWithOptimisticLock(UUID userId, BookingRequest request) {
        User user = findUserOrThrow(userId);
        Showtime showtime = findBookableShowtimeOrThrow(request.getShowtimeId());

        List<ShowtimeSeat> showtimeSeats = showtimeSeatRepository
                .findAllByShowtimeIdAndIdIn(request.getShowtimeId(), request.getSeatIds());
        if (showtimeSeats.size() != request.getSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats do not belong to this showtime");
        }
        validateAllAvailable(showtimeSeats);

        Booking booking = createPendingBooking(user, showtime, showtimeSeats.size());

        try {
            for (ShowtimeSeat showtimeSeat : showtimeSeats) {
                showtimeSeat.setStatus(ShowtimeSeatStatus.BOOKED);
                showtimeSeatRepository.saveAndFlush(showtimeSeat);
                saveBookingSeat(booking, showtimeSeat, showtime.getPrice());
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConflictException("One or more seats were just booked by someone else, please retry");
        }
        return buildResponse(booking);
    }

    public BookingResponse getById(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        List<BookingSeat> bookingSeats = bookingSeatRepository.findAllByBookingId(id);
        return new BookingResponse(booking, bookingSeats);
    }

    public BookingResponse confirm(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This booking does not belong to you");
        }

        if (booking.getStatus() == BookingStatus.PENDING && booking.getExpiresAt().isBefore(Instant.now())) {
            bookingExpiryWriter.expireAndPersist(booking);
            throw new ConflictException("This booking's hold has expired, please book again");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException(
                    "Booking is not in a confirmable state, current status: " + booking.getStatus());
        }

        PaymentChargeResponse payment = paymentClient.charge(booking.getId(), booking.getTotalAmount());
        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new PaymentDeclinedException("Payment was declined for booking " + booking.getId());
        }

        try {
            return bookingConfirmWriter.writeConfirmed(booking.getId(), payment.getPaymentId());
        } catch (RuntimeException e) {
            compensateFailedConfirm(booking.getId(), payment.getPaymentId());
            throw new ConflictException(
                    "Could not confirm this booking, any charge has been refunded: " + e.getMessage());
        }
    }

    public BookingResponse cancel(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This booking does not belong to you");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException(
                    "Only a confirmed booking can be cancelled, current status: " + booking.getStatus());
        }

        RefundChargeResponse refund = refundWithReconciliation(booking.getId(), booking.getPaymentId());

        try {
            return bookingCancelWriter.writeCancelled(bookingId, refund.getAmount());
        } catch (RuntimeException e) {
            compensationLogger.log(booking.getId(), booking.getPaymentId(),
                    CompensationType.REFUND_SUCCEEDED_BUT_CANCEL_WRITE_FAILED,
                    "Refund succeeded but writeCancelled() failed, booking may still show CONFIRMED: " + e.getMessage());
            throw new ConflictException(
                    "Refund succeeded but booking could not be updated, please contact support: " + e.getMessage());

        }
    }

    @Transactional
    public int expirePendingBookings() {
        List<Booking> candidates = bookingRepository
                .findAllByStatusAndExpiresAtBefore(BookingStatus.PENDING, Instant.now());

        int expiredCount = 0;
        for (Booking booking : candidates) {
            try {
                bookingExpiryWriter.expireAndPersist(booking);
                expiredCount++;
            } catch (ObjectOptimisticLockingFailureException ignored) {

            }
        }
        return expiredCount;
    }

    private void compensateFailedConfirm(UUID bookingId, UUID paymentId) {
        try {
            paymentClient.refund(paymentId);
        } catch (RuntimeException compensationFailure) {
            compensationLogger.log(bookingId, paymentId, CompensationType.CHARGE_COMPENSATION_FAILED,
                    "Charge succeeded but the confirm() write failed, and the compensating refund also failed: "
                            + compensationFailure.getMessage());
        }
    }

    private RefundChargeResponse refundWithReconciliation(UUID bookingId, UUID paymentId) {
        try {
            return paymentClient.refund(paymentId);
        } catch (PaymentServiceUnavailableException e) {
            Optional<RefundChargeResponse> reconciled = paymentClient.findRefundByPaymentId(paymentId);
            if (reconciled.isPresent()) {
                return reconciled.get();
            }
            compensationLogger.log(bookingId, paymentId, CompensationType.REFUND_RECONCILIATION_INCONCLUSIVE,
                    "refund() failed with an ambiguous error, and reconciliation could not confirm either way: "
                            + e.getMessage());
            throw e;
        }
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Showtime findBookableShowtimeOrThrow(UUID showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));
        if (showtime.isDeleted()) {
            throw new ConflictException("This showtime has been cancelled");
        }
        if (showtime.getStartTime().isBefore(Instant.now())) {
            throw new ConflictException("This showtime has already started");
        }
        return showtime;
    }

    private void validateAllAvailable(List<ShowtimeSeat> showtimeSeats) {
        for (ShowtimeSeat showtimeSeat : showtimeSeats) {
            if (showtimeSeat.getStatus() != ShowtimeSeatStatus.AVAILABLE) {
                throw new ConflictException(
                        "Seat " + showtimeSeat.getSeat().getRowLabel() + showtimeSeat.getSeat().getSeatNumber()
                                + " is no longer available");
            }
        }
    }

    private Booking createPendingBooking(User user, Showtime showtime, int seatCount) {
        BigDecimal total = showtime.getPrice().multiply(BigDecimal.valueOf(seatCount));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setExpiresAt(Instant.now().plus(HOLD_DURATION));
        booking.setTotalAmount(total);
        bookingRepository.save(booking);
        return booking;
    }

    private void saveBookingSeat(Booking booking, ShowtimeSeat showtimeSeat, BigDecimal price) {
        BookingSeat bookingSeat = new BookingSeat();
        bookingSeat.setBooking(booking);
        bookingSeat.setShowtimeSeat(showtimeSeat);
        bookingSeat.setPrice(price);
        bookingSeatRepository.save(bookingSeat);
    }

    private BookingResponse buildResponse(Booking booking) {
        List<BookingSeat> savedBookingSeats = bookingSeatRepository.findAllByBookingId(booking.getId());
        return new BookingResponse(booking, savedBookingSeats);
    }
}
