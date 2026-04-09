package com.example.demo.service;

import com.example.demo.dto.StudentProfileRequest;
import com.example.demo.dto.StudentProfileResponse;
import com.example.demo.entity.StudentProfile;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    public StudentProfileService(StudentProfileRepository studentProfileRepository,
                                 UserRepository userRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
    }

    public StudentProfileResponse getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return new StudentProfileResponse(
                profile.getUserId(),
                profile.getBiography()
        );
    }

    public StudentProfileResponse createProfile(StudentProfileRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (studentProfileRepository.existsById(user.getUserId())) {
            throw new RuntimeException("Student profile already exists");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUser(user);
        profile.setBiography(request.getBiography() == null ? "" : request.getBiography().trim());

        StudentProfile saved = studentProfileRepository.save(profile);

        return new StudentProfileResponse(
                saved.getUserId(),
                saved.getBiography()
        );
    }

    public StudentProfileResponse updateProfile(Long userId, StudentProfileRequest request) {
        StudentProfile profile = studentProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        profile.setBiography(request.getBiography() == null ? "" : request.getBiography().trim());

        StudentProfile saved = studentProfileRepository.save(profile);

        return new StudentProfileResponse(
                saved.getUserId(),
                saved.getBiography()
        );
    }
}