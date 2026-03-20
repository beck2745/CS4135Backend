package com.example.demo.repository;

import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudentId(Long studentId);
    List<Booking> findByTutorId(Long tutorId);
    List<Booking> findByTutorIdAndSessionDateAndStatus(Long tutorId, String sessionDate, BookingStatus status);
    
}
