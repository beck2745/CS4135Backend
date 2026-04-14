package com.skillswap.booking.dto;


public class StudentProfileRequest {
    private Long userId;
    private String biography;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}