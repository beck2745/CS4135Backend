package com.example.demo.entity;

import com.example.demo.valueobject.ContentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_content",
        uniqueConstraints = @UniqueConstraint(columnNames = {"content_type", "content_id"}))
public class BlockedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    // The admin who blocked this item
    @Column(nullable = false)
    private Long blockedByAdminId;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime blockedAt;

    public BlockedContent() {}

    public BlockedContent(ContentType contentType, Long contentId, Long blockedByAdminId, String reason) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.blockedByAdminId = blockedByAdminId;
        this.reason = reason;
        this.blockedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public ContentType getContentType() { return contentType; }

    public Long getContentId() { return contentId; }

    public Long getBlockedByAdminId() { return blockedByAdminId; }

    public String getReason() { return reason; }

    public LocalDateTime getBlockedAt() { return blockedAt; }
}
