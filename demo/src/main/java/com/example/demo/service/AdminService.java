package com.example.demo.service;

import com.example.demo.dto.AdminActionLogDTO;
import com.example.demo.dto.BlockedContentDTO;
import com.example.demo.dto.ReportResponseDTO;
import com.example.demo.entity.AdminActionLog;
import com.example.demo.entity.BlockedContent;
import com.example.demo.entity.Report;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AdminActionLogRepository;
import com.example.demo.repository.BlockedContentRepository;
import com.example.demo.repository.ReportRepository;
import com.example.demo.valueobject.AdminActionType;
import com.example.demo.valueobject.ContentType;
import com.example.demo.valueobject.ReportStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final ReportRepository reportRepository;
    private final AdminActionLogRepository actionLogRepository;
    private final BlockedContentRepository blockedContentRepository;

    public AdminService(ReportRepository reportRepository,
                        AdminActionLogRepository actionLogRepository,
                        BlockedContentRepository blockedContentRepository) {
        this.reportRepository = reportRepository;
        this.actionLogRepository = actionLogRepository;
        this.blockedContentRepository = blockedContentRepository;
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

    @Transactional
    public BlockedContentDTO blockContent(ContentType contentType, Long contentId, Long adminId, String reason) {
        if (blockedContentRepository.existsByContentTypeAndContentId(contentType, contentId)) {
            throw new ConflictException("This content is already blocked");
        }

        BlockedContent saved = blockedContentRepository.save(
                new BlockedContent(contentType, contentId, adminId, reason));

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.BLOCK_CONTENT, contentId, reason));

        return toBlockedDTO(saved);
    }

    @Transactional
    public void unblockContent(ContentType contentType, Long contentId, Long adminId, String reason) {
        BlockedContent blocked = blockedContentRepository.findByContentTypeAndContentId(contentType, contentId)
                .orElseThrow(() -> new ResourceNotFoundException("No block found for this content"));

        blockedContentRepository.delete(blocked);

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.UNBLOCK_CONTENT, contentId, reason));
    }

    @Transactional(readOnly = true)
    public List<BlockedContentDTO> getBlocked(ContentType contentType) {
        List<BlockedContent> results = contentType != null
                ? blockedContentRepository.findByContentTypeOrderByBlockedAtDesc(contentType)
                : blockedContentRepository.findAllByOrderByBlockedAtDesc();

        return results.stream().map(this::toBlockedDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(ContentType contentType, Long contentId) {
        return blockedContentRepository.existsByContentTypeAndContentId(contentType, contentId);
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

    private BlockedContentDTO toBlockedDTO(BlockedContent b) {
        return new BlockedContentDTO(
                b.getId(),
                b.getContentType(),
                b.getContentId(),
                b.getBlockedByAdminId(),
                b.getReason(),
                b.getBlockedAt()
        );
    }
}
