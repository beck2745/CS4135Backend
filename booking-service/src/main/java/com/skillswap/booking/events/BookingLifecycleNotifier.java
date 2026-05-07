package com.skillswap.booking.events;

import com.skillswap.booking.model.Booking;

/**
 * Publishes booking lifecycle signals for downstream consumers (event-driven evolution).
 * Current implementation logs structured domain events; a message broker can subscribe later.
 */
public interface BookingLifecycleNotifier {

    void bookingConfirmed(Booking booking);

    void bookingCompleted(Booking booking);
}
