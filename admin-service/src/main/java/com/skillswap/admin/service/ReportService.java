package com.skillswap.admin.service;

import com.skillswap.admin.dto.ReportRequestDTO;
import com.skillswap.admin.dto.ReportResponseDTO;
import com.skillswap.admin.entity.Report;
import com.skillswap.admin.exception.ConflictException;
import com.skillswap.admin.exception.ResourceNotFoundException;
import com.skillswap.admin.repository.ReportRepository;
import com.skillswap.admin.repository.UserRepository;
import com.skillswap.admin.valueobject.ContentType;
import com.skillswap.admin.valueobject.ReportStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReportResponseDTO submit(ReportRequestDTO dto) {
        if (dto.reportedByUserId() == null) {
            throw new ConflictException("reportedByUserId is required");
        }
        if (dto.contentType() == null) {
            throw new ConflictException("contentType is required");
        }
        if (dto.contentId() == null) {
            throw new ConflictException("contentId is required");
        }
        if (dto.reason() == null || dto.reason().isBlank()) {
            throw new ConflictException("reason is required");
        }

        userRepository.findById(dto.reportedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting user not found"));

        if (reportRepository.existsByReportedByUserIdAndContentTypeAndContentId(
                dto.reportedByUserId(), dto.contentType(), dto.contentId())) {
            throw new ConflictException("You have already reported this item");
        }

        Report saved = reportRepository.save(
                new Report(dto.reportedByUserId(), dto.contentType(), dto.contentId(), dto.reason().trim()));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getAll() {
        return reportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getByStatus(ReportStatus status) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getByContentType(ContentType contentType) {
        return reportRepository.findByContentTypeOrderByCreatedAtDesc(contentType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResponseDTO getById(Long id) {
        return toResponse(reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found")));
    }

    private ReportResponseDTO toResponse(Report r) {
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
}
