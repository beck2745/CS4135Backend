package com.skillswap.messaging.client.booking;

import com.skillswap.messaging.exception.ServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class BookingClientFallbackFactory implements FallbackFactory<BookingClient> {

    @Override
    public BookingClient create(Throwable cause) {
        return id -> {
            throw new ServiceUnavailableException(
                    "Booking service unavailable; cannot validate booking. " + cause.getMessage());
        };
    }
}
