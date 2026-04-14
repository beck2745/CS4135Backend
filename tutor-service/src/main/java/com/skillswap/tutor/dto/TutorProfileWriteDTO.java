package com.skillswap.tutor.dto;

import java.util.List;

public record TutorProfileWriteDTO(Long userId, String biography, List<TutorSkillDTO> skills) {}
