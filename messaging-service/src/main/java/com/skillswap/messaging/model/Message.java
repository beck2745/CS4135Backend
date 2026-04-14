package com.skillswap.messaging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    private Long threadId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private boolean blocked = false;

    public Message() {}

    public Message(Long threadId, Long senderId, String content, LocalDateTime sentAt) {
        this.threadId = threadId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
