package com.skillswap.tutor.client.booking;

/** Mirrors JSON from booking-service internal API (status as enum name string). */
public record BookingInternalDTO(Long id, Long studentId, Long tutorId, String status) {}
