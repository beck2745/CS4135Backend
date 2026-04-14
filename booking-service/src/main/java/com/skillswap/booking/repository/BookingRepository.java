package com.skillswap.booking.repository;

import com.skillswap.booking.model.Booking;
import com.skillswap.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudentId(Long studentId);

    List<Booking> findByTutorId(Long tutorId);

    List<Booking> findByTutorIdAndSessionDateAndStatus(Long tutorId, String sessionDate, BookingStatus status);

    List<Booking> findByStatusIn(List<BookingStatus> statuses);
}