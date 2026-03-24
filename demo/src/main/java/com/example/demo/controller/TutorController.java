package com.example.demo.controller;

import com.example.demo.dto.TutorProfileResponseDTO;
import com.example.demo.dto.TutorProfileWriteDTO;
import com.example.demo.dto.TutorSearchResultDTO;
import com.example.demo.service.TutorProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
@CrossOrigin(origins = "http://localhost:5173")
public class TutorController {

    private final TutorProfileService tutorProfileService;

    public TutorController(TutorProfileService tutorProfileService) {
        this.tutorProfileService = tutorProfileService;
    }

    @GetMapping("/search")
    public List<TutorSearchResultDTO> search(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Boolean verifiedOnly,
            @RequestParam(required = false) Double minRating
    ) {
        return tutorProfileService.search(skill, verifiedOnly, minRating);
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
            @RequestBody TutorProfileWriteDTO body
    ) {
        return tutorProfileService.update(tutorId, body);
    }
}
