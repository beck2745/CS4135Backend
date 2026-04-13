package com.skillswap.tutor.client.booking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", contextId = "bookingInternal", path = "/api/internal/bookings")
public interface BookingInternalClient {

    @GetMapping("/{bookingId}")
    BookingInternalDTO getBooking(@PathVariable("bookingId") Long bookingId);
}
