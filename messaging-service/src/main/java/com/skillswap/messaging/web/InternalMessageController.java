package com.skillswap.messaging.web;

import com.skillswap.messaging.exception.ResourceNotFoundException;
import com.skillswap.messaging.model.Message;
import com.skillswap.messaging.repository.MessageRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/messages")
public class InternalMessageController {

    private final MessageRepository messageRepository;

    public InternalMessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{id}")
    public Message getById(@PathVariable Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    @PostMapping("/{id}/block")
    public void block(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        message.setBlocked(true);
        messageRepository.save(message);
    }

    @PostMapping("/{id}/unblock")
    public void unblock(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        message.setBlocked(false);
        messageRepository.save(message);
    }
}
