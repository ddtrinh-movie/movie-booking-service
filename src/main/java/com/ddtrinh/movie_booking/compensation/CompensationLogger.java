package com.ddtrinh.movie_booking.compensation;

import com.ddtrinh.movie_booking.compensation.entity.CompensationLog;
import com.ddtrinh.movie_booking.compensation.entity.CompensationType;
import com.ddtrinh.movie_booking.compensation.repository.CompensationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompensationLogger {

    private final CompensationLogRepository compensationLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID bookingId, UUID paymentId, CompensationType type, String reason) {
        CompensationLog entry = new CompensationLog();
        entry.setBookingId(bookingId);
        entry.setPaymentId(paymentId);
        entry.setType(type);
        entry.setReason(reason);
        compensationLogRepository.save(entry);
    }
}
