package com.example.demo.service;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import com.example.demo.model.Message;
import com.example.demo.model.MessageThread;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.MessageThreadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final BookingRepository bookingRepository;

    public MessageService(MessageThreadRepository threadRepository,
            MessageRepository messageRepository,
            BookingRepository bookingRepository) {
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.bookingRepository = bookingRepository;
    }

    public MessageThread createThread(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("A message thread can only be created for a CONFIRMED booking");
        }

        if (threadRepository.existsByBookingId(bookingId)) {
            throw new ConflictException("A message thread already exists for this booking");
        }

        MessageThread thread = new MessageThread(bookingId, LocalDateTime.now());
        return threadRepository.save(thread);
    }

    public MessageThread getThreadByBookingId(Long bookingId) {
        return threadRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No message thread found for booking " + bookingId));
    }

    public MessageThread getThreadById(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Message thread not found"));
    }

    public Message sendMessage(Long threadId, Long senderId, String content) {
        MessageThread thread = getThreadById(threadId);

        // Fetch the booking to verify the sender is a participant
        Booking booking = bookingRepository.findById(thread.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isParticipant = senderId.equals(booking.getStudentId())
                || senderId.equals(booking.getTutorId());

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