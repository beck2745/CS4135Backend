package com.skillswap.admin.dto;

import com.skillswap.admin.valueobject.ContentType;
import com.skillswap.admin.valueobject.ReportStatus;

import java.time.LocalDateTime;

public record ReportResponseDTO(
        Long id,
        Long reportedByUserId,
        ContentType contentType,
        Long contentId,
        String reason,
        ReportStatus status,
        LocalDateTime createdAt
) {}
