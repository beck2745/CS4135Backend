package com.example.demo.service;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ServiceUnavailableException;
import com.example.demo.model.Booking;
import com.example.demo.model.BookingStatus;
import com.example.demo.model.Message;
import com.example.demo.model.MessageThread;
import com.example.demo.repository.BlockedContentRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.MessageThreadRepository;
import com.example.demo.valueobject.ContentType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static final long TIMEOUT_MS = 2_000;
    private static final int FAILURE_THRESHOLD = 3;
    private static final long RESET_TIMEOUT_MS = 30_000;

    private enum CircuitState { CLOSED, OPEN, HALF_OPEN }

    private final AtomicReference<CircuitState> circuitState =
            new AtomicReference<>(CircuitState.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong openedAt = new AtomicLong(0);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final MessageThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final BookingRepository bookingRepository;
    private final BlockedContentRepository blockedContentRepository;

    public MessageService(MessageThreadRepository threadRepository,
    public MessageService(MessageThreadRepository threadRepository,
                          MessageRepository messageRepository,
                          BookingRepository bookingRepository,
                          BlockedContentRepository blockedContentRepository) {
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.bookingRepository = bookingRepository;
        this.blockedContentRepository = blockedContentRepository;
    }

    private void checkCircuit() {
        CircuitState state = circuitState.get();

        if (state == CircuitState.OPEN) {
            long elapsed = System.currentTimeMillis() - openedAt.get();
            if (elapsed >= RESET_TIMEOUT_MS) {
                circuitState.compareAndSet(CircuitState.OPEN, CircuitState.HALF_OPEN);
                System.out.println("[CircuitBreaker] OPEN → HALF_OPEN after " + elapsed + " ms");
            } else {
                throw new ServiceUnavailableException(
                        "Message service is currently unavailable (circuit OPEN). " +
                        "Please try again in a few seconds.");
            }
        }
    }

    private void recordSuccess() {
        if (circuitState.get() == CircuitState.HALF_OPEN) {
            circuitState.set(CircuitState.CLOSED);
            System.out.println("[CircuitBreaker] HALF_OPEN → CLOSED (probe succeeded)");
        }
        failureCount.set(0);
    }

    private void recordFailure() {
        int failures = failureCount.incrementAndGet();
        System.err.println("[CircuitBreaker] Failure count: " + failures);

        if (failures >= FAILURE_THRESHOLD || circuitState.get() == CircuitState.HALF_OPEN) {
            circuitState.set(CircuitState.OPEN);
            openedAt.set(System.currentTimeMillis());
            System.err.println("[CircuitBreaker] OPEN — threshold reached after " +
                               failures + " failure(s)");
        }
    }

    private <T> T executeWithResilience(Callable<T> task) {
        checkCircuit();

        Future<T> future = executor.submit(task);
        try {
            T result = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            recordSuccess();
            return result;
        } catch (TimeoutException e) {
            future.cancel(true);
            recordFailure();
            System.err.println("[Timeout] Messaging DB call exceeded " + TIMEOUT_MS + " ms");
            throw new ServiceUnavailableException("Message service timed out. Please try again.");
        } catch (ExecutionException e) {
            recordFailure();
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            throw new RuntimeException(cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            recordFailure();
            throw new ServiceUnavailableException("Message service was interrupted.");
        }
    }

    public MessageThread createThread(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException(
                    "A message thread can only be created for a CONFIRMED booking");
        }

        if (threadRepository.existsByBookingId(bookingId)) {
            throw new ConflictException(
                    "A message thread already exists for this booking");
        }

        MessageThread thread = new MessageThread(bookingId, LocalDateTime.now());
        return threadRepository.save(thread);
    }

    public MessageThread getThreadByBookingId(Long bookingId) {
        return threadRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No message thread found for booking " + bookingId));
    }

    public MessageThread getThreadById(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Message thread not found"));
    }

    public Message sendMessage(Long threadId, Long senderId, String content) {

        return executeWithResilience(() -> {

            if (blockedContentRepository.existsByContentTypeAndContentId(ContentType.USER, senderId)) {
                throw new ConflictException("Your account has been blocked and cannot send messages");
            }

            MessageThread thread = getThreadById(threadId);

            Booking booking = bookingRepository.findById(thread.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

            boolean isParticipant = senderId.equals(booking.getStudentId())
                    || senderId.equals(booking.getTutorId());

            if (!isParticipant) {
                System.err.println(
                        "[WARN] Unauthorised message attempt: senderId=" + senderId +
                        " is not a participant of booking " + booking.getId() +
                        " (studentId=" + booking.getStudentId() +
                        ", tutorId=" + booking.getTutorId() + ")");

                throw new ConflictException(
                        "Only the student or tutor of this booking can send messages");
            }

            Message message = new Message(threadId, senderId, content, LocalDateTime.now());
            return messageRepository.save(message);
        });
    }

    public List<Message> getMessages(Long threadId) {

        return executeWithResilience(() -> {
            getThreadById(threadId);
            return messageRepository.findByThreadIdOrderBySentAtAsc(threadId).stream()
                    .filter(m -> !blockedContentRepository
                            .existsByContentTypeAndContentId(ContentType.MESSAGE, m.getMessageId()))
                    .collect(Collectors.toList());
        });
    }
}