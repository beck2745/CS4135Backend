package com.skillswap.booking.service;

import com.skillswap.booking.client.identity.IdentityClient;
import com.skillswap.booking.dto.StudentProfileRequest;
import com.skillswap.booking.dto.StudentProfileResponse;
import com.skillswap.booking.entity.StudentProfile;
import com.skillswap.booking.exception.ResourceNotFoundException;
import com.skillswap.booking.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final IdentityClient identityClient;

    public StudentProfileService(StudentProfileRepository studentProfileRepository, IdentityClient identityClient) {
        this.studentProfileRepository = studentProfileRepository;
        this.identityClient = identityClient;
    }

    private void ensureUser(Long userId) {
        Map<String, Boolean> res = identityClient.userExists(userId);
        if (!Boolean.TRUE.equals(res.get("exists"))) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    public StudentProfileResponse getProfileByUserId(Long userId) {
        StudentProfile profile =
                studentProfileRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return new StudentProfileResponse(profile.getUserId(), profile.getBiography());
    }

    public StudentProfileResponse createProfile(StudentProfileRequest request) {
        ensureUser(request.getUserId());
        if (studentProfileRepository.existsById(request.getUserId())) {
            throw new IllegalStateException("Student profile already exists");
        }
        StudentProfile profile = new StudentProfile();
        profile.setUserId(request.getUserId());
        profile.setBiography(request.getBiography() == null ? "" : request.getBiography().trim());
        StudentProfile saved = studentProfileRepository.save(profile);
        return new StudentProfileResponse(saved.getUserId(), saved.getBiography());
    }

    public StudentProfileResponse updateProfile(Long userId, StudentProfileRequest request) {
        StudentProfile profile =
                studentProfileRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        profile.setBiography(request.getBiography() == null ? "" : request.getBiography().trim());
        StudentProfile saved = studentProfileRepository.save(profile);
        return new StudentProfileResponse(saved.getUserId(), saved.getBiography());
    }
}