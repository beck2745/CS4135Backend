package com.skillswap.booking.scheduler;

import com.skillswap.booking.service.BookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusScheduler {

    private final BookingService bookingService;

    public BookingStatusScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 60000)
    public void markCompletedBookings() {
        bookingService.completeExpiredBookings();
    }
}