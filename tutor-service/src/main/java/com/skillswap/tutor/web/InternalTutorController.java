package com.skillswap.tutor.web;

import com.skillswap.tutor.repository.TutorProfileRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/tutors")
public class InternalTutorController {

    private final TutorProfileRepository tutorProfileRepository;

    public InternalTutorController(TutorProfileRepository tutorProfileRepository) {
        this.tutorProfileRepository = tutorProfileRepository;
    }

    @PostMapping("/{userId}/block")
    public void block(@PathVariable Long userId) {
        tutorProfileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setBlocked(true);
            tutorProfileRepository.save(profile);
        });
    }

    @PostMapping("/{userId}/unblock")
    public void unblock(@PathVariable Long userId) {
        tutorProfileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setBlocked(false);
            tutorProfileRepository.save(profile);
        });
    }
}
