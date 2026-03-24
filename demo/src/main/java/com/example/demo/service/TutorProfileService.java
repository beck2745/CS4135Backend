package com.example.demo.service;

import com.example.demo.dto.TutorProfileResponseDTO;
import com.example.demo.dto.TutorProfileWriteDTO;
import com.example.demo.dto.TutorSearchResultDTO;
import com.example.demo.dto.TutorSkillDTO;
import com.example.demo.entity.TutorProfile;
import com.example.demo.entity.TutorSkill;
import com.example.demo.entity.User;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.VerificationStatus;
import com.example.demo.repository.TutorProfileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.valueobject.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final UserRepository userRepository;

    public TutorProfileService(TutorProfileRepository tutorProfileRepository, UserRepository userRepository) {
        this.tutorProfileRepository = tutorProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TutorSearchResultDTO> search(String skill, Boolean verifiedOnly, Double minRating) {
        String skillFilter = skill != null ? skill.trim() : "";
        List<TutorProfile> candidates = skillFilter.isEmpty()
                ? tutorProfileRepository.findAll()
                : tutorProfileRepository.findDistinctBySkillNameContaining(skillFilter);

        boolean onlyVerified = Boolean.TRUE.equals(verifiedOnly);
        double min = minRating != null ? minRating : 0.0;

        return candidates.stream()
                .filter(p -> !onlyVerified || p.getVerificationStatus() == VerificationStatus.VERIFIED)
                .filter(p -> min <= 0
                        || (p.getAverageRating() != null && p.getAverageRating() >= min))
                .map(this::toSearchResult)
                .sorted(Comparator.comparing(TutorSearchResultDTO::userId))
                .collect(Collectors.toList());
    }

    private TutorSearchResultDTO toSearchResult(TutorProfile p) {
        User user = userRepository.findById(p.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for tutor profile"));
        return new TutorSearchResultDTO(
                p.getUserId(),
                user.getEmail(),
                p.getBiography(),
                mapSkills(p),
                p.getVerificationStatus(),
                p.getAverageRating()
        );
    }

    @Transactional(readOnly = true)
    public TutorProfileResponseDTO getByUserId(Long userId) {
        TutorProfile p = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));
        return toResponse(p);
    }

    @Transactional
    public TutorProfileResponseDTO create(TutorProfileWriteDTO dto) {
        if (dto.userId() == null) {
            throw new ConflictException("userId is required");
        }
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != UserRole.TUTOR) {
            throw new ConflictException("Only users with TUTOR role can create a tutor profile");
        }
        if (tutorProfileRepository.existsByUserId(dto.userId())) {
            throw new ConflictException("Tutor profile already exists for this user");
        }
        List<TutorSkill> skills = buildSkills(dto.skills());
        validateSkills(skills);

        TutorProfile profile = new TutorProfile();
        profile.setUserId(dto.userId());
        profile.setBiography(dto.biography() != null ? dto.biography() : "");
        profile.setVerificationStatus(VerificationStatus.PENDING);
        profile.setAverageRating(null);
        profile.replaceSkills(skills);

        TutorProfile saved = tutorProfileRepository.save(profile);
        return toResponse(saved);
    }

    @Transactional
    public TutorProfileResponseDTO update(Long userId, TutorProfileWriteDTO dto) {
        TutorProfile profile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found"));
        if (dto.userId() != null && !dto.userId().equals(userId)) {
            throw new ConflictException("userId in body must match path");
        }
        List<TutorSkill> skills = buildSkills(dto.skills());
        validateSkills(skills);

        profile.setBiography(dto.biography() != null ? dto.biography() : "");
        profile.replaceSkills(skills);

        TutorProfile saved = tutorProfileRepository.save(profile);
        return toResponse(saved);
    }

    private void validateSkills(List<TutorSkill> skills) {
        if (skills.isEmpty()) {
            throw new ConflictException("At least one skill with a name is required");
        }
        Set<String> seen = new HashSet<>();
        for (TutorSkill s : skills) {
            String key = s.getName().toLowerCase(Locale.ROOT);
            if (!seen.add(key)) {
                throw new ConflictException("Duplicate skill name: " + s.getName());
            }
        }
    }

    private List<TutorSkill> buildSkills(List<TutorSkillDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }
        List<TutorSkill> out = new ArrayList<>();
        for (TutorSkillDTO d : dtos) {
            if (d == null || d.name() == null || d.name().isBlank()) {
                continue;
            }
            String level = d.proficiencyLevel() != null ? d.proficiencyLevel().trim() : "";
            out.add(new TutorSkill(d.name().trim(), level.isEmpty() ? null : level));
        }
        return out;
    }

    private TutorProfileResponseDTO toResponse(TutorProfile p) {
        return new TutorProfileResponseDTO(
                p.getTutorProfileId(),
                p.getUserId(),
                p.getBiography(),
                mapSkills(p),
                p.getVerificationStatus(),
                p.getAverageRating()
        );
    }

    private List<TutorSkillDTO> mapSkills(TutorProfile p) {
        return p.getSkills().stream()
                .map(s -> new TutorSkillDTO(s.getName(), s.getProficiencyLevel()))
                .collect(Collectors.toList());
    }
}
