package com.example.demo.dto;

import java.util.List;

public record TutorProfileWriteDTO(Long userId, String biography, List<TutorSkillDTO> skills) {}
