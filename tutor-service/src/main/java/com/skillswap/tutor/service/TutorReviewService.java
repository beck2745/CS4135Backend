package com.skillswap.tutor.service;

import com.skillswap.tutor.client.booking.BookingInternalClient;
import com.skillswap.tutor.client.booking.BookingInternalDTO;
import com.skillswap.tutor.dto.CreateTutorReviewRequest;
import com.skillswap.tutor.exception.ResourceNotFoundException;
import com.skillswap.tutor.model.TutorReview;
import com.skillswap.tutor.repository.TutorReviewRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorReviewService {

    private final TutorReviewRepository tutorReviewRepository;
    private final BookingInternalClient bookingInternalClient;

    public TutorReviewService(
            TutorReviewRepository tutorReviewRepository,
            BookingInternalClient bookingInternalClient) {
        this.tutorReviewRepository = tutorReviewRepository;
        this.bookingInternalClient = bookingInternalClient;
    }

    public TutorReview createReview(CreateTutorReviewRequest request) {
        BookingInternalDTO booking = fetchBooking(request.getBookingId());
        if (!booking.studentId().equals(request.getStudentId())) {
            throw new IllegalArgumentException("You can only review your own booking");
        }
        if (!"COMPLETED".equals(booking.status())) {
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
        review.setBookingId(booking.id());
        review.setStudentId(booking.studentId());
        review.setTutorId(booking.tutorId());
        review.setRating(rating);
        review.setComment(request.getComment());
        return tutorReviewRepository.save(review);
    }

    private BookingInternalDTO fetchBooking(Long bookingId) {
        try {
            return bookingInternalClient.getBooking(bookingId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Booking not found");
            }
            throw e;
        }
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
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream()
                .mapToInt(TutorReview::getRating)
                .sum();
        return sum / reviews.size();
    }
}
