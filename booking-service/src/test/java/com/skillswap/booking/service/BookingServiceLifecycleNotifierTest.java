package com.skillswap.booking.service;

import com.skillswap.booking.client.identity.IdentityClient;
import com.skillswap.booking.events.BookingLifecycleNotifier;
import com.skillswap.booking.model.Booking;
import com.skillswap.booking.model.BookingStatus;
import com.skillswap.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceLifecycleNotifierTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private IdentityClient identityClient;

    @Mock
    private BookingLifecycleNotifier bookingLifecycleNotifier;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, identityClient, bookingLifecycleNotifier);
    }

    @Test
    void approveBooking_emitsBookingConfirmed() throws Exception {
        LocalDate future = LocalDate.now().plusDays(7);
        Booking pending = new Booking();
        setBookingId(pending, 50L);
        pending.setStudentId(1L);
        pending.setTutorId(2L);
        pending.setSessionDate(future);
        pending.setStartTime(LocalTime.of(10, 0));
        pending.setEndTime(LocalTime.of(11, 0));
        pending.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findByStatusIn(anyList())).thenReturn(List.of());
        when(bookingRepository.findById(50L)).thenReturn(Optional.of(pending));
        when(bookingRepository.findByTutorIdAndSessionDateAndStatus(
                        pending.getTutorId(), future, BookingStatus.CONFIRMED))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bookingService.approveBooking(50L);

        verify(bookingLifecycleNotifier).bookingConfirmed(any(Booking.class));
    }

    @Test
    void completeBooking_emitsBookingCompleted() throws Exception {
        LocalDate future = LocalDate.now().plusDays(1);
        Booking confirmed = new Booking();
        setBookingId(confirmed, 51L);
        confirmed.setStudentId(1L);
        confirmed.setTutorId(2L);
        confirmed.setSessionDate(future);
        confirmed.setStartTime(LocalTime.of(9, 0));
        confirmed.setEndTime(LocalTime.of(10, 0));
        confirmed.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findByStatusIn(anyList())).thenReturn(List.of());
        when(bookingRepository.findById(51L)).thenReturn(Optional.of(confirmed));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bookingService.completeBooking(51L);

        verify(bookingLifecycleNotifier).bookingCompleted(any(Booking.class));
    }

    private static void setBookingId(Booking booking, long id) throws Exception {
        Field f = Booking.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(booking, id);
    }
}
