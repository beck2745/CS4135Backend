package com.example.demo.entity;

import com.example.demo.valueobject.AdminActionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_action_log")
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminActionType actionType;

    // The report this action was taken on
    @Column(nullable = false)
    private Long reportId;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    public AdminActionLog() {}

    public AdminActionLog(Long adminId, AdminActionType actionType, Long reportId, String notes) {
        this.adminId = adminId;
        this.actionType = actionType;
        this.reportId = reportId;
        this.notes = notes;
        this.performedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getAdminId() { return adminId; }

    public AdminActionType getActionType() { return actionType; }

    public Long getReportId() { return reportId; }

    public String getNotes() { return notes; }

    public LocalDateTime getPerformedAt() { return performedAt; }
}
