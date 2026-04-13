package com.skillswap.messaging.client.booking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", contextId = "booking", fallbackFactory = BookingClientFallbackFactory.class)
public interface BookingClient {

    @GetMapping("/api/internal/bookings/{id}")
    BookingInternalDTO getBooking(@PathVariable("id") Long id);
}
