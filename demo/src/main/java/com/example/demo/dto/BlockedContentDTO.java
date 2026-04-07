package com.example.demo.dto;

import com.example.demo.valueobject.ContentType;

import java.time.LocalDateTime;

public record BlockedContentDTO(
        Long id,
        ContentType contentType,
        Long contentId,
        Long blockedByAdminId,
        String reason,
        LocalDateTime blockedAt
) {}
