package com.skillswap.booking.service;

import com.skillswap.booking.client.identity.IdentityClient;
import com.skillswap.booking.exception.ConflictException;
import com.skillswap.booking.exception.ResourceNotFoundException;
import com.skillswap.booking.model.Booking;
import com.skillswap.booking.model.BookingStatus;
import com.skillswap.booking.repository.BookingRepository;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final IdentityClient identityClient;

    public BookingService(BookingRepository bookingRepository, IdentityClient identityClient) {
        this.bookingRepository = bookingRepository;
        this.identityClient = identityClient;
    }

    private void ensureUserExists(Long userId) {
        Map<String, Boolean> res = identityClient.userExists(userId);
        if (!Boolean.TRUE.equals(res.get("exists"))) {
            throw new ResourceNotFoundException("User not found for id " + userId);
        }
    }

    public Booking createBooking(Booking booking) {
        ensureUserExists(booking.getStudentId());
        ensureUserExists(booking.getTutorId());
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<Booking> getStudentBookings(Long studentId) {
        completeExpiredBookings();
        return bookingRepository.findByStudentId(studentId);
    }

    public List<Booking> getTutorBookings(Long tutorId) {
        completeExpiredBookings();
        return bookingRepository.findByTutorId(tutorId);
    }

    public Booking getBookingById(Long id) {
        completeExpiredBookings();
        return bookingRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    public Booking approveBooking(Long id) {
        Booking booking = getBookingById(id);
        boolean conflict =
                hasConflict(
                        booking.getTutorId(),
                        booking.getSessionDate(),
                        booking.getStartTime(),
                        booking.getEndTime());
        if (conflict) {
            throw new ConflictException("Time slot already booked");
        }
        if (impossibleTimeSlot(booking.getSessionDate(), booking.getStartTime(), booking.getEndTime())) {
            throw new ConflictException("Invalid time slot");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public Booking rejectBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.COMPLETED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getTutorSchedule(Long tutorId, LocalDate sessionDate) {
        completeExpiredBookings();
        return bookingRepository.findByTutorIdAndSessionDateAndStatus(
                tutorId, sessionDate, BookingStatus.CONFIRMED);
    }

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Dublin");

public void completeExpiredBookings() {
    List<Booking> activeBookings =
            bookingRepository.findByStatusIn(List.of(BookingStatus.CONFIRMED));

    LocalDateTime now = LocalDateTime.now(APP_ZONE);

    for (Booking booking : activeBookings) {
        if (hasBookingEnded(booking, now)) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);
        }
    }
}

    private boolean hasBookingEnded(Booking booking, LocalDateTime now) {
        LocalDate sessionDate=booking.getSessionDate();
        LocalTime endTime=booking.getEndTime();
        if(sessionDate==null || endTime==null) {
            return false;
        }
        try{
            LocalDate date = sessionDate;
            LocalTime time = endTime;
            LocalDateTime bookingEnd = LocalDateTime.of(date, time);
            return !now.isBefore(bookingEnd);
        } catch (DateTimeParseException e) {
            return false;
        }
    
    
}

    private boolean hasConflict(Long tutorId, LocalDate sessionDate, LocalTime startTime, LocalTime endTime) {
    List<Booking> approvedBookings =
            bookingRepository.findByTutorIdAndSessionDateAndStatus(
                    tutorId,
                    sessionDate,
                    BookingStatus.CONFIRMED
            );

    for (Booking existing : approvedBookings) {
        if (startTime.isBefore(existing.getEndTime()) &&
            endTime.isAfter(existing.getStartTime())) {
            return true;
        }
    }

    return false;
}

    private boolean impossibleTimeSlot(LocalDate sessionDate, LocalTime startTime, LocalTime endTime) {
        try {
            LocalDate date = sessionDate;
            LocalTime start = startTime;
            LocalTime end = endTime;
            return !start.isBefore(end) || date.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return true;
        }
    }
}