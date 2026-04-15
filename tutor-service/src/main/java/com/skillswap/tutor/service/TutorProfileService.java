package com.skillswap.tutor.service;

import com.skillswap.tutor.client.identity.IdentityUserClient;
import com.skillswap.tutor.client.identity.dto.UserPublicDTO;
import com.skillswap.tutor.dto.TutorProfileResponseDTO;
import com.skillswap.tutor.dto.TutorProfileWriteDTO;
import com.skillswap.tutor.dto.TutorSearchResultDTO;
import com.skillswap.tutor.dto.TutorSkillDTO;
import com.skillswap.tutor.entity.TutorProfile;
import com.skillswap.tutor.entity.TutorSkill;
import com.skillswap.tutor.exception.ConflictException;
import com.skillswap.tutor.exception.ResourceNotFoundException;
import com.skillswap.tutor.model.VerificationStatus;
import com.skillswap.tutor.repository.TutorProfileRepository;
import com.skillswap.tutor.repository.TutorSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final TutorSkillRepository tutorSkillRepository;
    private final IdentityUserClient identityUserClient;

    public TutorProfileService(
            TutorProfileRepository tutorProfileRepository,
            TutorSkillRepository tutorSkillRepository,
            IdentityUserClient identityUserClient) {
        this.tutorProfileRepository = tutorProfileRepository;
        this.tutorSkillRepository = tutorSkillRepository;
        this.identityUserClient = identityUserClient;
    }

    @Transactional(readOnly = true)
    public List<String> listDistinctSkillNames() {
        Collection<String> seen = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        seen.addAll(tutorSkillRepository.findDistinctSkillNames());
        List<String> out = new ArrayList<>(seen);
        out.sort(String.CASE_INSENSITIVE_ORDER);
        return out;
    }

    @Transactional(readOnly = true)
    public List<TutorSearchResultDTO> search(
            String skill,
            Boolean verifiedOnly,
            Double minRating,
            String q,
            String proficiencyLevel) {
        String skillFilter = skill != null ? skill.trim() : "";
        List<TutorProfile> candidates = skillFilter.isEmpty()
                ? tutorProfileRepository.findByBlockedFalse()
                : tutorProfileRepository.findDistinctBySkillNameContaining(skillFilter);

        boolean onlyVerified = Boolean.TRUE.equals(verifiedOnly);
        double min = minRating != null ? minRating : 0.0;
        String query = q != null ? q.trim() : "";
        String profFilter = proficiencyLevel != null ? proficiencyLevel.trim() : "";

        List<TutorProfile> narrowed = candidates.stream()
                .filter(p -> !onlyVerified || p.getVerificationStatus() == VerificationStatus.VERIFIED)
                .filter(p -> min <= 0
                        || (p.getAverageRating() != null && p.getAverageRating() >= min))
                .filter(p -> profFilter.isEmpty() || hasProficiency(p, profFilter))
                .collect(Collectors.toList());

        if (query.isEmpty()) {
            return toSortedSearchResults(narrowed);
        }

        Map<Long, UserPublicDTO> usersById = loadUsersById(narrowed);
        return narrowed.stream()
                .filter(p -> {
                    UserPublicDTO user = usersById.get(p.getUserId());
                    return user != null && matchesQuery(p, user, query);
                })
                .sorted(Comparator.comparing(TutorProfile::getUserId))
                .map(p -> toSearchResult(p, usersById.get(p.getUserId())))
                .collect(Collectors.toList());
    }

    private List<TutorSearchResultDTO> toSortedSearchResults(List<TutorProfile> profiles) {
        Map<Long, UserPublicDTO> usersById = loadUsersById(profiles);
        return profiles.stream()
                .sorted(Comparator.comparing(TutorProfile::getUserId))
                .map(p -> toSearchResult(p, usersById.get(p.getUserId())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<Long, UserPublicDTO> loadUsersById(List<TutorProfile> profiles) {
        List<Long> ids = profiles.stream().map(TutorProfile::getUserId).distinct().toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        return identityUserClient.resolveUsers(ids).stream()
                .collect(Collectors.toMap(UserPublicDTO::userId, u -> u, (a, b) -> a));
    }

    private static boolean hasProficiency(TutorProfile p, String filter) {
        String f = filter.toUpperCase(Locale.ROOT);
        return p.getSkills().stream().anyMatch(s -> {
            String level = s.getProficiencyLevel();
            if (level == null || level.isBlank()) {
                return false;
            }
            String l = level.trim().toUpperCase(Locale.ROOT);
            return l.equals(f) || l.contains(f);
        });
    }

    private static boolean matchesQuery(TutorProfile p, UserPublicDTO user, String qRaw) {
        String needle = qRaw.toLowerCase(Locale.ROOT);
        if (user.email() != null && user.email().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (user.name() != null && user.name().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (p.getBiography() != null && p.getBiography().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        return p.getSkills().stream().anyMatch(s -> skillTextMatchesQuery(s, needle));
    }

    private static boolean skillTextMatchesQuery(TutorSkill s, String needle) {
        if (s.getName() != null && s.getName().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (s.getCategory() != null && s.getCategory().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (s.getSubcategory() != null && s.getSubcategory().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        return s.getExperienceNote() != null
                && s.getExperienceNote().toLowerCase(Locale.ROOT).contains(needle);
    }

    private TutorSearchResultDTO toSearchResult(TutorProfile p, UserPublicDTO user) {
        if (user == null) {
            return null;
        }
        return new TutorSearchResultDTO(
                p.getUserId(),
                user.email(),
                p.getBiography(),
                mapSkills(p),
                p.getVerificationStatus(),
                p.getAverageRating());
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
        List<UserPublicDTO> resolved = identityUserClient.resolveUsers(List.of(dto.userId()));
        boolean userExists = resolved.stream().anyMatch(u -> u.userId().equals(dto.userId()));
        if (!userExists) {
            throw new ResourceNotFoundException("User not found");
        }
        // Monolith enforced UserRole.TUTOR here; validate via identity extension or API gateway when available.
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
            String cat = blankToNull(d.category());
            String sub = blankToNull(d.subcategory());
            String note = blankToNull(d.experienceNote());
            out.add(new TutorSkill(
                    d.name().trim(),
                    level.isEmpty() ? null : level,
                    cat,
                    sub,
                    note));
        }
        return out;
    }

    private static String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private TutorProfileResponseDTO toResponse(TutorProfile p) {
        return new TutorProfileResponseDTO(
                p.getTutorProfileId(),
                p.getUserId(),
                p.getBiography(),
                mapSkills(p),
                p.getVerificationStatus(),
                p.getAverageRating());
    }

    private List<TutorSkillDTO> mapSkills(TutorProfile p) {
        return p.getSkills().stream()
                .map(s -> new TutorSkillDTO(
                        s.getName(),
                        s.getProficiencyLevel(),
                        s.getCategory(),
                        s.getSubcategory(),
                        s.getExperienceNote()))
                .collect(Collectors.toList());
    }
}
