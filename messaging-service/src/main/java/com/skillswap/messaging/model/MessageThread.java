package com.skillswap.messaging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_threads")
public class MessageThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long threadId;

    @Column(nullable = false, unique = true)
    private Long bookingId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public MessageThread() {}

    public MessageThread(Long bookingId, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.createdAt = createdAt;
    }

    public Long getThreadId() {
        return threadId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
