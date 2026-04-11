package com.example.demo.repository;

import com.example.demo.model.TutorReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TutorReviewRepository extends JpaRepository<TutorReview, Long> {
    Optional<TutorReview> findByBookingId(Long bookingId);
    List<TutorReview> findByTutorId(Long tutorId);
    boolean existsByBookingId(Long bookingId);
}