package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.StudentProfileRequest;
import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.service.StudentProfileService;
import com.example.demo.entity.StudentProfile;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.StudentProfileRepository;

@RestController
@RequestMapping("/api/student-profiles")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentProfileController {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public StudentProfileController(StudentProfileRepository studentProfileRepository,
                                    UserRepository userRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long userId) {
        StudentProfile profile = studentProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return ResponseEntity.ok(
                new StudentProfileResponse(profile.getUserId(), profile.getBiography())
        );
    }

  @PostMapping
    public ResponseEntity<StudentProfileResponse> createStudentProfile(
            @RequestBody StudentProfileRequest request
    ) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (studentProfileRepository.existsById(request.getUserId())) {
            throw new RuntimeException("Student profile already exists");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUser(user);
        profile.setBiography(request.getBiography() == null ? "" : request.getBiography().trim());

        StudentProfile saved = studentProfileRepository.save(profile);

        return ResponseEntity.ok(
                new StudentProfileResponse(saved.getUserId(), saved.getBiography())
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfileRequest request
    ) {
        StudentProfile profile = studentProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        profile.setBiography(request.getBiography() == null ? "" : request.getBiography().trim());

        StudentProfile saved = studentProfileRepository.save(profile);

        return ResponseEntity.ok(
                new StudentProfileResponse(saved.getUserId(), saved.getBiography())
        );
    }
}