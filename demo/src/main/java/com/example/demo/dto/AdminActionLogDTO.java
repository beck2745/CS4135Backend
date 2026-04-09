package com.example.demo.dto;

import com.example.demo.valueobject.AdminActionType;

import java.time.LocalDateTime;

public record AdminActionLogDTO(
        Long id,
        Long adminId,
        AdminActionType actionType,
        Long reportId,
        String notes,
        LocalDateTime performedAt
) {}
