package com.example.demo.dto;

import com.example.demo.model.VerificationStatus;

import java.util.List;

public record TutorSearchResultDTO(
        Long userId,
        String email,
        String biography,
        List<TutorSkillDTO> skills,
        VerificationStatus verificationStatus,
        Double averageRating
) {}
