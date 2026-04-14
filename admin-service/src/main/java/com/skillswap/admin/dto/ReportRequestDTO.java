package com.skillswap.admin.dto;

import com.skillswap.admin.valueobject.ContentType;

public record ReportRequestDTO(
        Long reportedByUserId,
        ContentType contentType,
        Long contentId,
        String reason
) {}
