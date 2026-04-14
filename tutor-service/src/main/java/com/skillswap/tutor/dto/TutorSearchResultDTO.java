package com.skillswap.tutor.dto;

import com.skillswap.tutor.model.VerificationStatus;

import java.util.List;

public record TutorSearchResultDTO(
        Long userId,
        String email,
        String biography,
        List<TutorSkillDTO> skills,
        VerificationStatus verificationStatus,
        Double averageRating) {}
