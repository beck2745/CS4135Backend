package com.example.demo.dto;

import com.example.demo.model.VerificationStatus;

import java.util.List;

public record TutorProfileResponseDTO(
        Long tutorProfileId,
        Long userId,
        String biography,
        List<TutorSkillDTO> skills,
        VerificationStatus verificationStatus,
        Double averageRating
) {}
