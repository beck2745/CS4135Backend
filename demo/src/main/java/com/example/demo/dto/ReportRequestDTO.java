package com.example.demo.dto;

import com.example.demo.valueobject.ContentType;

public record ReportRequestDTO(
        Long reportedByUserId,
        ContentType contentType,
        Long contentId,
        String reason
) {}
