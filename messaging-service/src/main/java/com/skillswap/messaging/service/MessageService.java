package com.skillswap.messaging.service;

import com.skillswap.messaging.client.booking.BookingClient;
import com.skillswap.messaging.client.booking.BookingInternalDTO;
import com.skillswap.messaging.exception.ConflictException;
import com.skillswap.messaging.exception.ResourceNotFoundException;
import com.skillswap.messaging.model.Message;
import com.skillswap.messaging.model.MessageThread;
import com.skillswap.messaging.repository.MessageRepository;
import com.skillswap.messaging.repository.MessageThreadRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final BookingClient bookingClient;

    public MessageService(
            MessageThreadRepository threadRepository,
            MessageRepository messageRepository,
            BookingClient bookingClient) {
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.bookingClient = bookingClient;
    }

    private BookingInternalDTO fetchBooking(Long bookingId) {
        try {
            return bookingClient.getBooking(bookingId);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Booking not found");
            }
            throw e;
        }
    }

    public MessageThread createThread(Long bookingId) {
        BookingInternalDTO booking = fetchBooking(bookingId);
        if (!"CONFIRMED".equals(booking.status())) {
            throw new ConflictException("A message thread can only be created for a CONFIRMED booking");
        }
        if (threadRepository.existsByBookingId(bookingId)) {
            throw new ConflictException("A message thread already exists for this booking");
        }
        MessageThread thread = new MessageThread(bookingId, LocalDateTime.now());
        return threadRepository.save(thread);
    }

    public MessageThread getThreadByBookingId(Long bookingId) {
        return threadRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No message thread found for booking " + bookingId));
    }

    public MessageThread getThreadById(Long threadId) {
        return threadRepository
                .findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Message thread not found"));
    }

    public Message sendMessage(Long threadId, Long senderId, String content) {
        MessageThread thread = getThreadById(threadId);
        BookingInternalDTO booking = fetchBooking(thread.getBookingId());
        boolean isParticipant = senderId.equals(booking.studentId()) || senderId.equals(booking.tutorId());
        if (!isParticipant) {
            throw new ConflictException("Only the student or tutor of this booking can send messages");
        }
        Message message = new Message(threadId, senderId, content, LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getMessages(Long threadId) {
        getThreadById(threadId);
        return messageRepository.findByThreadIdOrderBySentAtAsc(threadId);
    }
}
