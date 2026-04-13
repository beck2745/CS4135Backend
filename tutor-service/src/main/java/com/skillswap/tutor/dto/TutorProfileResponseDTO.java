package com.skillswap.tutor.dto;

import com.skillswap.tutor.model.VerificationStatus;

import java.util.List;

public record TutorProfileResponseDTO(
        Long tutorProfileId,
        Long userId,
        String biography,
        List<TutorSkillDTO> skills,
        VerificationStatus verificationStatus,
        Double averageRating) {}
