package com.skillswap.admin.dto;

import com.skillswap.admin.valueobject.ContentType;

import java.time.LocalDateTime;

public record BlockedContentDTO(
        Long id,
        ContentType contentType,
        Long contentId,
        Long blockedByAdminId,
        String reason,
        LocalDateTime blockedAt
) {}
