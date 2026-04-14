package com.skillswap.admin.service;

import com.skillswap.admin.dto.AdminActionLogDTO;
import com.skillswap.admin.dto.BlockedContentDTO;
import com.skillswap.admin.dto.ReportResponseDTO;
import com.skillswap.admin.entity.AdminActionLog;
import com.skillswap.admin.entity.BlockedContent;
import com.skillswap.admin.entity.Report;
import com.skillswap.admin.exception.ConflictException;
import com.skillswap.admin.exception.ResourceNotFoundException;
import com.skillswap.admin.repository.AdminActionLogRepository;
import com.skillswap.admin.repository.BlockedContentRepository;
import com.skillswap.admin.repository.ReportRepository;
import com.skillswap.admin.valueobject.AdminActionType;
import com.skillswap.admin.valueobject.ContentType;
import com.skillswap.admin.valueobject.ReportStatus;
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
    public ReportResponseDTO blockReport(Long reportId, Long adminId, String notes) {
        Report report = getOpenReport(reportId);

        if (!blockedContentRepository.existsByContentTypeAndContentId(report.getContentType(), report.getContentId())) {
            blockedContentRepository.save(
                    new BlockedContent(report.getContentType(), report.getContentId(), adminId, notes));
        }

        report.setStatus(ReportStatus.CLOSED);
        Report saved = reportRepository.save(report);

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.BLOCK_CONTENT, reportId, notes));

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

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.BLOCK_CONTENT, null, reason));

        return toBlockedDTO(saved);
    }

    @Transactional
    public void unblockContent(ContentType contentType, Long contentId, Long adminId, String reason) {
        BlockedContent blocked = blockedContentRepository.findByContentTypeAndContentId(contentType, contentId)
                .orElseThrow(() -> new ResourceNotFoundException("No block found for this content"));

        blockedContentRepository.delete(blocked);

        actionLogRepository.save(new AdminActionLog(adminId, AdminActionType.UNBLOCK_CONTENT, null, reason));
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
                r.getCreatedAt());
    }

    private AdminActionLogDTO toLogDTO(AdminActionLog log) {
        return new AdminActionLogDTO(
                log.getId(),
                log.getAdminId(),
                log.getActionType(),
                log.getReportId(),
                log.getNotes(),
                log.getPerformedAt());
    }

    private BlockedContentDTO toBlockedDTO(BlockedContent b) {
        return new BlockedContentDTO(
                b.getId(),
                b.getContentType(),
                b.getContentId(),
                b.getBlockedByAdminId(),
                b.getReason(),
                b.getBlockedAt());
    }
}
