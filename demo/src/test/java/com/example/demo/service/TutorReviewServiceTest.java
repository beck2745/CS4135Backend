package com.example.demo.service;

import com.example.demo.dto.CreateTutorReviewRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import com.example.demo.model.TutorReview;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.TutorReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorReviewServiceTest {

    @Mock
    private TutorReviewRepository tutorReviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private TutorReviewService tutorReviewService;

    private Booking completedBooking;
    private CreateTutorReviewRequest validRequest;

    @BeforeEach
    void setUp() {
        // Use the all-args constructor — Booking has no setId()
        completedBooking = new Booking(
                1L, 10L, 20L, "Math", "2025-06-01", "10:00", "11:00", 60, null,
                BookingStatus.COMPLETED
        );

        validRequest = new CreateTutorReviewRequest();
        validRequest.setBookingId(1L);
        validRequest.setStudentId(10L);
        validRequest.setRating(5);
        validRequest.setComment("Great tutor!");
    }

    // ── createReview ────────────────────────────────────────────────────────

    @Test
    void createReview_savesAndReturnsReview_whenRequestIsValid() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));
        when(tutorReviewRepository.existsByBookingId(1L)).thenReturn(false);

        TutorReview saved = new TutorReview();
        saved.setRating(5);
        when(tutorReviewRepository.save(any(TutorReview.class))).thenReturn(saved);

        TutorReview result = tutorReviewService.createReview(validRequest);

        assertThat(result.getRating()).isEqualTo(5);
        verify(tutorReviewRepository).save(any(TutorReview.class));
    }

    @Test
    void createReview_throwsResourceNotFound_whenBookingDoesNotExist() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorReviewService.createReview(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void createReview_throwsIllegalArgument_whenStudentDoesNotOwnBooking() {
        Booking otherStudentsBooking = new Booking(
                1L, 99L, 20L, "Math", "2025-06-01", "10:00", "11:00", 60, null,
                BookingStatus.COMPLETED
        );
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(otherStudentsBooking));

        assertThatThrownBy(() -> tutorReviewService.createReview(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("your own booking");
    }

    @Test
    void createReview_throwsIllegalArgument_whenBookingIsNotCompleted() {
        Booking confirmedBooking = new Booking(
                1L, 10L, 20L, "Math", "2025-06-01", "10:00", "11:00", 60, null,
                BookingStatus.CONFIRMED
        );
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(confirmedBooking));

        assertThatThrownBy(() -> tutorReviewService.createReview(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("completed bookings");
    }

    @Test
    void createReview_throwsIllegalArgument_whenBookingAlreadyReviewed() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));
        when(tutorReviewRepository.existsByBookingId(1L)).thenReturn(true);

        assertThatThrownBy(() -> tutorReviewService.createReview(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already been reviewed");
    }

    @Test
    void createReview_throwsIllegalArgument_whenRatingIsZero() {
        validRequest.setRating(0);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));
        when(tutorReviewRepository.existsByBookingId(1L)).thenReturn(false);

        assertThatThrownBy(() -> tutorReviewService.createReview(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rating must be between 1 and 5");
    }

    @Test
    void createReview_throwsIllegalArgument_whenRatingExceedsFive() {
        validRequest.setRating(6);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));
        when(tutorReviewRepository.existsByBookingId(1L)).thenReturn(false);

        assertThatThrownBy(() -> tutorReviewService.createReview(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rating must be between 1 and 5");
    }

    // ── getAverageRating ─────────────────────────────────────────────────────

    @Test
    void getAverageRating_returnsCorrectAverage() {
        TutorReview r1 = new TutorReview(); r1.setRating(4);
        TutorReview r2 = new TutorReview(); r2.setRating(2);
        when(tutorReviewRepository.findByTutorId(20L)).thenReturn(List.of(r1, r2));

        double avg = tutorReviewService.getAverageRating(20L);

        assertThat(avg).isEqualTo(3.0);
    }

    @Test
    void getAverageRating_returnsZero_whenNoReviewsExist() {
        when(tutorReviewRepository.findByTutorId(20L)).thenReturn(List.of());

        double avg = tutorReviewService.getAverageRating(20L);

        assertThat(avg).isEqualTo(0.0);
    }

    // ── getTutorReviews ──────────────────────────────────────────────────────

    @Test
    void getTutorReviews_returnsListFromRepository() {
        TutorReview review = new TutorReview();
        when(tutorReviewRepository.findByTutorId(20L)).thenReturn(List.of(review));

        List<TutorReview> results = tutorReviewService.getTutorReviews(20L);

        assertThat(results).hasSize(1);
    }
}
