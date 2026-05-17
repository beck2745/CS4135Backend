package com.skillswap.tutor.service;

import com.skillswap.tutor.client.booking.BookingInternalClient;
import com.skillswap.tutor.client.booking.BookingInternalDTO;
import com.skillswap.tutor.dto.CreateTutorReviewRequest;
import com.skillswap.tutor.entity.TutorProfile;
import com.skillswap.tutor.model.TutorReview;
import com.skillswap.tutor.repository.TutorProfileRepository;
import com.skillswap.tutor.repository.TutorReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorReviewServiceTest {

    @Mock
    private TutorReviewRepository tutorReviewRepository;

    @Mock
    private TutorProfileRepository tutorProfileRepository;

    @Mock
    private BookingInternalClient bookingInternalClient;

    private TutorReviewService tutorReviewService;

    @BeforeEach
    void setUp() {
        tutorReviewService =
                new TutorReviewService(tutorReviewRepository, tutorProfileRepository, bookingInternalClient);
    }

    @Test
    void createReview_usesAuthenticatedStudentNotBodyStudent() {
        when(bookingInternalClient.getBooking(9L))
                .thenReturn(new BookingInternalDTO(9L, 1L, 2L, "COMPLETED"));
        when(tutorReviewRepository.existsByBookingId(9L)).thenReturn(false);

        TutorReview saved = new TutorReview();
        saved.setRating(5);
        when(tutorReviewRepository.save(any(TutorReview.class))).thenAnswer(inv -> inv.getArgument(0));

        TutorProfile profile = new TutorProfile();
        profile.setUserId(2L);
        when(tutorProfileRepository.findByUserId(2L)).thenReturn(Optional.of(profile));
        when(tutorReviewRepository.findByTutorId(2L)).thenReturn(List.of());

        CreateTutorReviewRequest req = new CreateTutorReviewRequest();
        req.setBookingId(9L);
        req.setStudentId(999L);
        req.setRating(5);
        req.setComment("great");

        tutorReviewService.createReview(req, 1L);

        ArgumentCaptor<TutorReview> cap = ArgumentCaptor.forClass(TutorReview.class);
        verify(tutorReviewRepository).save(cap.capture());
        assertEquals(1L, cap.getValue().getStudentId());
    }

    @Test
    void createReview_rejectsWhenAuthenticatedUserDoesNotOwnBooking() {
        when(bookingInternalClient.getBooking(9L))
                .thenReturn(new BookingInternalDTO(9L, 1L, 2L, "COMPLETED"));

        CreateTutorReviewRequest req = new CreateTutorReviewRequest();
        req.setBookingId(9L);
        req.setRating(5);

        assertThrows(IllegalArgumentException.class, () -> tutorReviewService.createReview(req, 99L));
    }
}
