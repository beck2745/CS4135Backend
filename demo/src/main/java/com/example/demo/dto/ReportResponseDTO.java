package com.example.demo.dto;

import com.example.demo.valueobject.ContentType;
import com.example.demo.valueobject.ReportStatus;

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
