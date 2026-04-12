package com.example.demo.dto;

public class StudentProfileResponse {
    
    private Long userId;
    private String biography;

    public StudentProfileResponse( Long userId, String biography) {
     
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