package com.example.demo.service;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import com.example.demo.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking pendingBooking;

    @BeforeEach
    void setUp() {
        pendingBooking = new Booking(
                1L, 10L, 20L, "Math",
                "2099-12-01", "10:00", "11:00",
                60, null, BookingStatus.PENDING
        );
    }

    // ── createBooking ────────────────────────────────────────────────────────

    @Test
    void createBooking_setsPendingStatusAndSaves() {
        Booking input = new Booking(
                null, 10L, 20L, "Math",
                "2099-12-01", "10:00", "11:00",
                60, null, null
        );
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(input);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
        verify(bookingRepository).save(input);
    }

    // ── approveBooking ───────────────────────────────────────────────────────

    @Test
    void approveBooking_setsConfirmedStatus_whenNoConflict() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.findByTutorIdAndSessionDateAndStatus(
                20L, "2099-12-01", BookingStatus.CONFIRMED))
                .thenReturn(List.of());
        when(bookingRepository.findByStatusIn(any())).thenReturn(List.of());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.approveBooking(1L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void approveBooking_throwsConflict_whenSlotAlreadyBooked() {
        Booking existing = new Booking(
                2L, 11L, 20L, "Math",
                "2099-12-01", "09:30", "10:30",
                60, null, BookingStatus.CONFIRMED
        );
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.findByTutorIdAndSessionDateAndStatus(
                20L, "2099-12-01", BookingStatus.CONFIRMED))
                .thenReturn(List.of(existing));
        when(bookingRepository.findByStatusIn(any())).thenReturn(List.of());

        assertThatThrownBy(() -> bookingService.approveBooking(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void approveBooking_throwsResourceNotFound_whenBookingMissing() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        when(bookingRepository.findByStatusIn(any())).thenReturn(List.of());

        assertThatThrownBy(() -> bookingService.approveBooking(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── rejectBooking ────────────────────────────────────────────────────────

    @Test
    void rejectBooking_setsRejectedStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.findByStatusIn(any())).thenReturn(List.of());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.rejectBooking(1L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    // ── cancelBooking ────────────────────────────────────────────────────────

    @Test
    void cancelBooking_setsCancelledStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.findByStatusIn(any())).thenReturn(List.of());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.cancelBooking(1L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    // ── completeExpiredBookings ──────────────────────────────────────────────

    @Test
    void completeExpiredBookings_completesBookingWhoseEndTimeIsInThePast() {
        // A booking that ended yesterday
        String yesterday = LocalDate.now().minusDays(1).toString();
        Booking expired = new Booking(
                3L, 10L, 20L, "Math",
                yesterday, "09:00", "10:00",
                60, null, BookingStatus.CONFIRMED
        );
        when(bookingRepository.findByStatusIn(List.of(BookingStatus.CONFIRMED)))
                .thenReturn(List.of(expired));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bookingService.completeExpiredBookings();

        assertThat(expired.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        verify(bookingRepository).save(expired);
    }

    @Test
    void completeExpiredBookings_doesNotCompleteBookingInTheFuture() {
        // A booking scheduled for tomorrow
        String tomorrow = LocalDate.now().plusDays(1).toString();
        Booking future = new Booking(
                4L, 10L, 20L, "Math",
                tomorrow, "10:00", "11:00",
                60, null, BookingStatus.CONFIRMED
        );
        when(bookingRepository.findByStatusIn(List.of(BookingStatus.CONFIRMED)))
                .thenReturn(List.of(future));

        bookingService.completeExpiredBookings();

        assertThat(future.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(bookingRepository, never()).save(future);
    }
}
