package com.skillswap.booking.events;

import com.skillswap.booking.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingBookingLifecycleNotifier implements BookingLifecycleNotifier {

    private static final Logger log = LoggerFactory.getLogger(LoggingBookingLifecycleNotifier.class);

    @Override
    public void bookingConfirmed(Booking booking) {
        log.info(
                "DOMAIN_EVENT type=BookingConfirmed bookingId={} studentId={} tutorId={} status={}",
                booking.getId(),
                booking.getStudentId(),
                booking.getTutorId(),
                booking.getStatus());
    }

    @Override
    public void bookingCompleted(Booking booking) {
        log.info(
                "DOMAIN_EVENT type=BookingCompleted bookingId={} studentId={} tutorId={} status={}",
                booking.getId(),
                booking.getStudentId(),
                booking.getTutorId(),
                booking.getStatus());
    }
}
