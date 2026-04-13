package com.skillswap.booking.web;

import com.skillswap.booking.dto.StudentProfileRequest;
import com.skillswap.booking.dto.StudentProfileResponse;
import com.skillswap.booking.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-profiles")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(studentProfileService.getProfileByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<StudentProfileResponse> createStudentProfile(@RequestBody StudentProfileRequest request) {
        return ResponseEntity.ok(studentProfileService.createProfile(request));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            @PathVariable Long userId, @RequestBody StudentProfileRequest request) {
        return ResponseEntity.ok(studentProfileService.updateProfile(userId, request));
    }
}