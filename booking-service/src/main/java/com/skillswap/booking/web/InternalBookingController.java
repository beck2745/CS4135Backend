package com.skillswap.booking.web;

import com.skillswap.booking.dto.BookingInternalDTO;
import com.skillswap.booking.model.Booking;
import com.skillswap.booking.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/bookings")
public class InternalBookingController {

    private final BookingService bookingService;

    public InternalBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public BookingInternalDTO get(@PathVariable Long id) {
        Booking b = bookingService.getBookingById(id);
        return new BookingInternalDTO(b.getId(), b.getStudentId(), b.getTutorId(), b.getStatus().name());
    }
}