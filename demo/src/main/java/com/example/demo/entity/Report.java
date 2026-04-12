package com.example.demo.entity;

import com.example.demo.valueobject.ContentType;
import com.example.demo.valueobject.ReportStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who submitted the report
    @Column(nullable = false)
    private Long reportedByUserId;

    // What type of content is being reported
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    // The ID of the reported item (userId, messageId, bookingId, tutorProfileId)
    @Column(nullable = false)
    private Long contentId;

    @Column(length = 2000, nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Report() {}

    public Report(Long reportedByUserId, ContentType contentType, Long contentId, String reason) {
        this.reportedByUserId = reportedByUserId;
        this.contentType = contentType;
        this.contentId = contentId;
        this.reason = reason;
        this.status = ReportStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getReportedByUserId() { return reportedByUserId; }

    public ContentType getContentType() { return contentType; }

    public Long getContentId() { return contentId; }

    public String getReason() { return reason; }

    public ReportStatus getStatus() { return status; }

    public void setStatus(ReportStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
