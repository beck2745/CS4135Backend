package com.skillswap.messaging.client.booking;

/**
 * Published language contract v1 — messaging-service depends on this.
 * Breaking changes require coordinated release.
 */
public record BookingInternalDTO(Long id, Long studentId, Long tutorId, String status) {}
