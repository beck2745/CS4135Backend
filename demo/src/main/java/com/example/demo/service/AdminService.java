package com.example.demo.service;

import com.example.demo.dto.AdminActionLogDTO;
import com.example.demo.dto.ReportResponseDTO;
import com.example.demo.entity.AdminActionLog;
import com.example.demo.entity.Report;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AdminActionLogRepository;
import com.example.demo.repository.ReportRepository;
import com.example.demo.valueobject.AdminActionType;
import com.example.demo.valueobject.ReportStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final ReportRepository reportRepository;
    private final AdminActionLogRepository actionLogRepository;

    public AdminService(ReportRepository reportRepository,
                        AdminActionLogRepository actionLogRepository) {
        this.reportRepository = reportRepository;
        this.actionLogRepository = actionLogRepository;
    }

    @Transactional
    public ReportResponseDTO reviewReport(Long reportId, Long adminId, String notes) {
        Report report = getOpenReport(reportId);

        report.setStatus(ReportStatus.REVIEWED);
        Report saved = reportRepository.save(report);

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.REVIEW_REPORT, reportId, notes));

        return toReportResponse(saved);
    }

    @Transactional
    public ReportResponseDTO dismissReport(Long reportId, Long adminId, String notes) {
        Report report = getOpenReport(reportId);

        report.setStatus(ReportStatus.DISMISSED);
        Report saved = reportRepository.save(report);

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.DISMISS_REPORT, reportId, notes));

        return toReportResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AdminActionLogDTO> getAuditLog() {
        return actionLogRepository.findAllByOrderByPerformedAtDesc().stream()
                .map(this::toLogDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminActionLogDTO> getAuditLogForReport(Long reportId) {
        reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        return actionLogRepository.findByReportIdOrderByPerformedAtDesc(reportId).stream()
                .map(this::toLogDTO)
                .collect(Collectors.toList());
    }

    // Only OPEN reports can be acted on
    private Report getOpenReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        if (report.getStatus() != ReportStatus.OPEN) {
            throw new ConflictException("Report has already been " + report.getStatus().name().toLowerCase());
        }
        return report;
    }

    private ReportResponseDTO toReportResponse(Report r) {
        return new ReportResponseDTO(
                r.getId(),
                r.getReportedByUserId(),
                r.getContentType(),
                r.getContentId(),
                r.getReason(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }

    private AdminActionLogDTO toLogDTO(AdminActionLog log) {
        return new AdminActionLogDTO(
                log.getId(),
                log.getAdminId(),
                log.getActionType(),
                log.getReportId(),
                log.getNotes(),
                log.getPerformedAt()
        );
    }
}
