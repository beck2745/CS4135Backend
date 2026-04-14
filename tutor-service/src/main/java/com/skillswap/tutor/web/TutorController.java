package com.skillswap.tutor.web;

import com.skillswap.tutor.dto.TutorProfileResponseDTO;
import com.skillswap.tutor.dto.TutorProfileWriteDTO;
import com.skillswap.tutor.dto.TutorSearchResultDTO;
import com.skillswap.tutor.service.TutorProfileService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
@CrossOrigin(origins = "http://localhost:5173")
public class TutorController {

    private final TutorProfileService tutorProfileService;

    public TutorController(TutorProfileService tutorProfileService) {
        this.tutorProfileService = tutorProfileService;
    }

    @GetMapping("/skills")
    public List<String> listSkillNames() {
        return tutorProfileService.listDistinctSkillNames();
    }

    @GetMapping("/search")
    public List<TutorSearchResultDTO> search(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Boolean verifiedOnly,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String proficiencyLevel) {
        return tutorProfileService.search(skill, verifiedOnly, minRating, q, proficiencyLevel);
    }

    @GetMapping("/profile/{tutorId}")
    public TutorProfileResponseDTO getProfile(@PathVariable Long tutorId) {
        return tutorProfileService.getByUserId(tutorId);
    }

    @PostMapping("/profile")
    public TutorProfileResponseDTO createProfile(@RequestBody TutorProfileWriteDTO body) {
        return tutorProfileService.create(body);
    }

    @PutMapping("/profile/{tutorId}")
    public TutorProfileResponseDTO updateProfile(
            @PathVariable Long tutorId,
            @RequestBody TutorProfileWriteDTO body) {
        return tutorProfileService.update(tutorId, body);
    }
}
