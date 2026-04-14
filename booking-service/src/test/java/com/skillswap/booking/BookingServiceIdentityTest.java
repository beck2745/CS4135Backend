package com.skillswap.booking;

import com.skillswap.booking.client.identity.IdentityClient;
import com.skillswap.booking.exception.ResourceNotFoundException;
import com.skillswap.booking.model.Booking;
import com.skillswap.booking.model.BookingStatus;
import com.skillswap.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class BookingServiceIdentityTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private IdentityClient identityClient;

    @Test
    void createBookingFailsWhenStudentMissingInIdentity() {
        when(identityClient.userExists(1L)).thenReturn(Map.of("exists", false));

        Booking b = new Booking();
        b.setStudentId(1L);
        b.setTutorId(2L);
        b.setSessionDate(LocalDate.of(2099, 1, 1));
        b.setStartTime(LocalTime.of(10, 0));
        b.setEndTime(LocalTime.of(11, 0));

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(b));
    }

    @Test
    void createBookingPersistsWhenIdentityReturnsExists() {
        when(identityClient.userExists(anyLong())).thenReturn(Map.of("exists", true));

        Booking b = new Booking();
        b.setStudentId(10L);
        b.setTutorId(20L);
        b.setSessionDate(LocalDate.of(2099, 2, 1));
        b.setStartTime(LocalTime.of(9, 0));
        b.setEndTime(LocalTime.of(10, 0));

        Booking saved = bookingService.createBooking(b);

        assertEquals(BookingStatus.PENDING, saved.getStatus());
    }
}