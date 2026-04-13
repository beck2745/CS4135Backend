package com.skillswap.messaging.web;

import com.skillswap.messaging.model.Message;
import com.skillswap.messaging.model.MessageThread;
import com.skillswap.messaging.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    @PostMapping("/threads")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageThread createThread(@RequestParam Long bookingId) {
        return messageService.createThread(bookingId);
    }

    @GetMapping("/threads/booking/{bookingId}")
    public MessageThread getThreadByBooking(@PathVariable Long bookingId) {
        return messageService.getThreadByBookingId(bookingId);
    }

    @GetMapping("/threads/{threadId}")
    public MessageThread getThread(@PathVariable Long threadId) {
        return messageService.getThreadById(threadId);
    }

    @PostMapping("/threads/{threadId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message sendMessage(@PathVariable Long threadId, @RequestBody Map<String, Object> body) {
        Long senderId = Long.valueOf(body.get("senderId").toString());
        String content = body.get("content").toString();
        return messageService.sendMessage(threadId, senderId, content);
    }

    @GetMapping("/threads/{threadId}/messages")
    public List<Message> getMessages(@PathVariable Long threadId) {
        return messageService.getMessages(threadId);
    }
}
