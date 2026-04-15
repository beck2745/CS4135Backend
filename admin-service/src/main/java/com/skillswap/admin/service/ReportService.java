package com.skillswap.admin.service;

import com.skillswap.admin.dto.ReportRequestDTO;
import com.skillswap.admin.dto.ReportResponseDTO;
import com.skillswap.admin.entity.Report;
import com.skillswap.admin.exception.ConflictException;
import com.skillswap.admin.exception.ResourceNotFoundException;
import com.skillswap.admin.repository.ReportRepository;
import com.skillswap.admin.valueobject.ContentType;
import com.skillswap.admin.valueobject.ReportStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final RestTemplate restTemplate;

    public ReportService(ReportRepository reportRepository, RestTemplate restTemplate) {
        this.reportRepository = reportRepository;
        this.restTemplate = restTemplate;
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

        if (reportRepository.existsByReportedByUserIdAndContentTypeAndContentId(
                dto.reportedByUserId(), dto.contentType(), dto.contentId())) {
            throw new ConflictException("You have already reported this item");
        }

        Report saved = reportRepository.save(
                new Report(dto.reportedByUserId(), dto.contentType(), dto.contentId(), dto.reason().trim()));

        return toResponse(saved, null);
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getAll() {
        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();
        return enrich(reports);
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getByStatus(ReportStatus status) {
        List<Report> reports = reportRepository.findByStatusOrderByCreatedAtDesc(status);
        return enrich(reports);
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getByContentType(ContentType contentType) {
        List<Report> reports = reportRepository.findByContentTypeOrderByCreatedAtDesc(contentType);
        return enrich(reports);
    }

    @Transactional(readOnly = true)
    public ReportResponseDTO getById(Long id) {
        Report r = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        List<ReportResponseDTO> enriched = enrich(List.of(r));
        return enriched.get(0);
    }

    private List<ReportResponseDTO> enrich(List<Report> reports) {
        if (reports.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = reports.stream()
                .map(Report::getReportedByUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> emailById = resolveEmails(userIds);

        return reports.stream()
                .map(r -> toResponse(r, emailById.get(r.getReportedByUserId())))
                .collect(Collectors.toList());
    }

    private Map<Long, String> resolveEmails(List<Long> userIds) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "http://identity-service/api/internal/users/resolve",
                    HttpMethod.POST,
                    new org.springframework.http.HttpEntity<>(userIds),
                    new ParameterizedTypeReference<>() {});
            List<Map<String, Object>> body = response.getBody();
            if (body == null) return Map.of();
            return body.stream()
                    .filter(u -> u.get("userId") != null && u.get("email") != null)
                    .collect(Collectors.toMap(
                            u -> ((Number) u.get("userId")).longValue(),
                            u -> (String) u.get("email"),
                            (a, b) -> a));
        } catch (Exception e) {
            return Map.of();
        }
    }

    private ReportResponseDTO toResponse(Report r, String email) {
        return new ReportResponseDTO(
                r.getId(),
                r.getReportedByUserId(),
                email,
                r.getContentType(),
                r.getContentId(),
                r.getReason(),
                r.getStatus(),
                r.getCreatedAt());
    }
}
