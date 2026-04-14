package com.skillswap.admin.dto;

import com.skillswap.admin.valueobject.AdminActionType;

import java.time.LocalDateTime;

public record AdminActionLogDTO(
        Long id,
        Long adminId,
        AdminActionType actionType,
        Long reportId,
        String notes,
        LocalDateTime performedAt
) {}
