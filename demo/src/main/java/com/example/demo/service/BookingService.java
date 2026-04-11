package com.example.demo.service;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import com.example.demo.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(timeout = 5)
    public Booking createBooking(Booking booking){
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<Booking> getStudentBookings(Long studentId){
        completeExpiredBookings();
        return bookingRepository.findByStudentId(studentId);
    }

    public List<Booking> getTutorBookings(Long tutorId){
        completeExpiredBookings();
        return bookingRepository.findByTutorId(tutorId);
    }

    public Booking getBookingById(Long id){
        completeExpiredBookings();
        return bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Transactional(timeout = 5)
    public Booking approveBooking(Long id) {
        Booking booking = getBookingById(id);

        boolean conflict = hasConflict(
            booking.getTutorId(),
            booking.getSessionDate(),
            booking.getStartTime(),
            booking.getEndTime()
        );

        if (conflict) {
            throw new ConflictException("Time slot already booked");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional(timeout = 5)
    public Booking rejectBooking(Long id){
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Transactional(timeout = 5)
    public Booking cancelBooking(Long id){
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Transactional(timeout = 5)
    public Booking completeBooking(Long id){
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.COMPLETED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getTutorSchedule(Long tutorId, String sessionDate){
        completeExpiredBookings();
        return bookingRepository.findByTutorIdAndSessionDateAndStatus(
            tutorId,
            sessionDate,
            BookingStatus.CONFIRMED
        );
    }

    public void completeExpiredBookings() {
        List<Booking> activeBookings = bookingRepository.findByStatusIn(
            List.of(BookingStatus.CONFIRMED)
        );

        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : activeBookings) {
            if (hasBookingEnded(booking, now)) {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);
            }
        }
    }

    private boolean hasBookingEnded(Booking booking, LocalDateTime now) {
        try {
            LocalDate date = LocalDate.parse(booking.getSessionDate());   // expects yyyy-MM-dd
            LocalTime endTime = LocalTime.parse(booking.getEndTime());    // expects HH:mm
            LocalDateTime bookingEnd = LocalDateTime.of(date, endTime);

            return now.isAfter(bookingEnd);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean hasConflict(Long tutorId, String sessionDate, String startTime, String endTime){
        List<Booking> approvedBookings = bookingRepository.findByTutorIdAndSessionDateAndStatus(
            tutorId,
            sessionDate,
            BookingStatus.CONFIRMED
        );

        for(Booking existing : approvedBookings){
            if (startTime.compareTo(existing.getEndTime()) < 0 &&
                endTime.compareTo(existing.getStartTime()) > 0) {
                return true;
            }
        }
        return false;
    }
}