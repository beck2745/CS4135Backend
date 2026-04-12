package com.example.demo.service;

import com.example.demo.dto.CreateTutorReviewRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import com.example.demo.model.TutorReview;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.TutorReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorReviewService {

    private final TutorReviewRepository tutorReviewRepository;
    private final BookingRepository bookingRepository;

    public TutorReviewService(TutorReviewRepository tutorReviewRepository,
                              BookingRepository bookingRepository) {
        this.tutorReviewRepository = tutorReviewRepository;
        this.bookingRepository = bookingRepository;
    }

    public TutorReview createReview(CreateTutorReviewRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getStudentId().equals(request.getStudentId())) {
            throw new IllegalArgumentException("You can only review your own booking");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("You can only review completed bookings");
        }

        if (tutorReviewRepository.existsByBookingId(request.getBookingId())) {
            throw new IllegalArgumentException("This booking has already been reviewed");
        }

        Integer rating = request.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        TutorReview review = new TutorReview();
        review.setBookingId(booking.getId());
        review.setStudentId(booking.getStudentId());
        review.setTutorId(booking.getTutorId());
        review.setRating(rating);
        review.setComment(request.getComment());

        return tutorReviewRepository.save(review);
    }

    public List<TutorReview> getTutorReviews(Long tutorId) {
        return tutorReviewRepository.findByTutorId(tutorId);
    }

    public TutorReview getReviewByBookingId(Long bookingId) {
        return tutorReviewRepository.findByBookingId(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    public double getAverageRating(Long tutorId) {
        List<TutorReview> reviews = tutorReviewRepository.findByTutorId(tutorId);

        if (reviews.isEmpty()) return 0.0;

        double sum = reviews.stream()
            .mapToInt(TutorReview::getRating)
            .sum();

        return sum / reviews.size();
    }
}