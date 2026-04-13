package com.skillswap.booking.dto;

public class StudentProfileResponse {
    private final Long userId;
    private final String biography;

    public StudentProfileResponse(Long userId, String biography) {
        this.userId = userId;
        this.biography = biography;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBiography() {
        return biography;
    }
}