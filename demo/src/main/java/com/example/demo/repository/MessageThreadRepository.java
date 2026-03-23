package com.example.demo.repository;

import com.example.demo.model.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    Optional<MessageThread> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
}